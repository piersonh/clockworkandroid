package com.wordco.clockworkandroid.timer_feature.ui.timer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.ui.timer.Second
import com.wordco.clockworkandroid.core.ui.timer.TimerState
import com.wordco.clockworkandroid.timer_feature.domain.use_case.AddMarkerUseCase
import com.wordco.clockworkandroid.timer_feature.ui.util.complete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

class TimerService() : Service() {

    private val binder = TimerBinder()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var wakeLock: PowerManager.WakeLock? = null

    private var notificationManager: TimerNotificationManager? = null
    private lateinit var taskRepository: TaskRepository
    private lateinit var addMarkerUseCase: AddMarkerUseCase


    private enum class State {
        INIT, DORMANT, PREPARING, RUNNING, PAUSED, CLOSING
    }

    private val _internalState = MutableStateFlow(State.INIT)

    private val _loadedTaskId: MutableStateFlow<Long?> = MutableStateFlow(null)

    private var _loadedTask: StateFlow<StartedTask>? = null
    private var timer: SessionTimer? = null
    private var collectionJob: Job? = null

    private val _state = MutableStateFlow<TimerState>(TimerState.Dormant)
    val state = _state.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        taskRepository = (application as MainApplication).taskRepository
        addMarkerUseCase = AddMarkerUseCase()

        val permissionRequestSignaller = (application as MainApplication).permissionRequestSignaller
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            coroutineScope.launch {
                if (permissionRequestSignaller.request(
                    Manifest.permission.POST_NOTIFICATIONS
                )) {
                    notificationManager = TimerNotificationManager(
                        this@TimerService,
                    )
                }
            }
        } else {
            notificationManager = TimerNotificationManager(
                this,
            )
        }

        setState(State.DORMANT)
    }

    @SuppressLint("WakelockTimeout") // we don't want a timeout for a stopwatch
    private fun acquireWakeLock() {
        if (wakeLock == null) {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "TimerService::Wakelock"
            )
            wakeLock?.setReferenceCounted(false)
            wakeLock?.acquire()
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.takeIf { it.isHeld }?.release()
        wakeLock = null
    }

    override fun onDestroy() {
        releaseWakeLock()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_SUSPEND = "ACTION_SUSPEND"
        const val ACTION_FINISH = "ACTION_FINISH"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
        const val ACTION_MARKER = "ACTION_MARKER"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = notificationManager?.buildPreparingNotification()
            ?: return START_NOT_STICKY

        startForeground(TimerNotificationManager.NOTIFICATION_ID, notification)

        when (intent?.action) {
            ACTION_START -> {
                val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
                if (taskId != -1L) {
                    start(taskId)
                }
            }
            ACTION_RESUME -> resume()
            ACTION_PAUSE -> pause()
            ACTION_SUSPEND -> {
                val replaceWithId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
                suspend(if (replaceWithId != -1L) replaceWithId else null)
            }
            ACTION_FINISH -> finish()
            ACTION_STOP_SERVICE -> stop()
            ACTION_MARKER -> addMarker()
        }

        return START_STICKY
    }

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    private fun stop() {
        setState(State.DORMANT)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun setState(state: State) {
        when (state) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.CLOSING -> {
                collectionJob?.cancel()
                _internalState.update { state }
                collectionJob = coroutineScope.launch {
                    collectInactive()
                }
            }
            State.RUNNING,
            State.PAUSED -> {
                collectionJob?.cancel()
                _internalState.update { state }
                collectionJob = coroutineScope.launch {
                    collectActive()
                }
            }
        }
    }


    private suspend fun collectActive() {
        combine (
            _internalState,
            _loadedTask!!,
            timer!!.elapsedWorkSeconds,
            timer!!.elapsedWorkMinutes,
        ) { taskState, loadedTask, workTime, breakTime ->
            when (taskState) {
                State.RUNNING -> TimerState.Running(
                    task = loadedTask,
                    elapsedWorkSeconds = workTime,
                    elapsedBreakMinutes = breakTime
                )
                State.PAUSED -> TimerState.Paused(
                    task = loadedTask,
                    elapsedWorkSeconds = workTime,
                    elapsedBreakMinutes = breakTime
                )
                else -> error("timer is not active")
            }
        }.collect { state ->
            _state.update { state }
            notificationManager?.showNotification(state)
        }
    }

    private suspend fun collectInactive() {
        combine (
            _internalState,
            _loadedTaskId,
        ) { taskState, taskId, ->
            when (taskState) {
                State.DORMANT -> TimerState.Dormant
                State.INIT,
                State.PREPARING -> TimerState.Preparing(taskId?:error(
                    "taskId must be set before timer can enter State.PREPARING"
                ))
                State.CLOSING -> TimerState.Closing
                else -> error ("timer is active")
            }
        }.collect { state ->
            _state.update { state }
        }
    }


    private fun prepareAndStart(taskId: Long) {
        setPreparing(taskId)
        coroutineScope.launch {
            val flow = taskRepository.getTask(taskId)
            val session = flow.first()

            when (session) {
                is CompletedTask -> error("Attempted to load completed session")
                is NewTask -> {
                    startIncrementer(
                        SessionTimer(
                            coroutineScope,
                            initialWorkSeconds = 0,
                            initialBreakMinutes = 0,
                        )
                    )
                    val session = session.start()
                    _loadedTask = flow.map { it as StartedTask }
                        .stateIn(
                            scope = coroutineScope,
                            started = SharingStarted.WhileSubscribed(),
                            initialValue = session
                        )
                    setState(State.RUNNING)
                }

                is StartedTask -> {
                    val (workSeconds, breakMinutes) = loadElapsedTimes(
                        workTime = session.workTime,
                        breakTime = session.breakTime,
                        lastSegment = session.segments.last()
                    )

                    when (session.status()) {
                        StartedTask.Status.SUSPENDED -> {
                            startIncrementer(
                                SessionTimer(
                                    coroutineScope = coroutineScope,
                                    initialWorkSeconds = workSeconds,
                                    initialBreakMinutes = breakMinutes,
                                )
                            )
                            session.endLastSegmentAndStartNew(Segment.Type.WORK)
                            _loadedTask = flow.map { it as StartedTask }.run {
                                stateIn(
                                    scope = coroutineScope,
                                    started = SharingStarted.WhileSubscribed(),
                                    initialValue = first()
                                )
                            }

                            setState(State.RUNNING)
                        }

                        StartedTask.Status.RUNNING -> {
                            startIncrementer(
                                SessionTimer(
                                    coroutineScope = coroutineScope,
                                    initialWorkSeconds = workSeconds,
                                    initialBreakMinutes = breakMinutes,
                                )
                            )
                            _loadedTask = flow.map { it as StartedTask }
                                .stateIn(
                                    scope = coroutineScope,
                                    started = SharingStarted.WhileSubscribed(),
                                    initialValue = session
                                )
                            setState(State.RUNNING)
                        }

                        StartedTask.Status.PAUSED -> {
                            startIncrementer(
                                SessionTimer(
                                    coroutineScope = coroutineScope,
                                    initialWorkSeconds = workSeconds,
                                    initialBreakMinutes = breakMinutes,
                                    startAsBreak = true
                                )
                            )
                            _loadedTask = flow.map { it as StartedTask }
                                .stateIn(
                                    scope = coroutineScope,
                                    started = SharingStarted.WhileSubscribed(),
                                    initialValue = session
                                )
                            setState(State.PAUSED)
                        }
                    }
                }
            }
        }
    }

    private suspend fun NewTask.start() : StartedTask {
        val segment = Segment(
            segmentId = 0,
            taskId = taskId,
            startTime = Instant.now(),
            duration = null,
            type = Segment.Type.WORK
        )

        val task = StartedTask(
            taskId = taskId,
            profileId = profileId,
            name = name,
            dueDate = dueDate,
            difficulty = difficulty,
            color = color,
            userEstimate = userEstimate,
            segments = listOf(segment),
            markers = emptyList(),
            appEstimate = appEstimate,
        )

        taskRepository.insertSegment(segment)
        taskRepository.updateTask(task)

        return task
    }

    private fun loadElapsedTimes(
        workTime: Duration,
        breakTime: Duration,
        lastSegment: Segment
    ) : Pair<Second, Int> {
        val sinceStarted = Duration.between(lastSegment.startTime, Instant.now()).seconds

        return when (lastSegment.type) {
            Segment.Type.WORK -> {
                Pair(
                    first = workTime.seconds.plus(sinceStarted).toInt(),
                    second = breakTime.toMinutes().toInt()
                )
            }
            Segment.Type.BREAK -> {
                Pair(
                    first = workTime.seconds.toInt(),
                    second = breakTime.toMinutes().plus(sinceStarted / 60).toInt()
                )
            }
            Segment.Type.SUSPEND -> {
                Pair(
                    first = workTime.seconds.toInt(),
                    second = breakTime.toMinutes().toInt()
                )
            }
        }
    }

    private suspend fun StartedTask.endLastSegmentAndStartNew(type: Segment.Type) {
        val now = Instant.now()
        val lastSegment = segments.last().run {
            copy(duration = Duration.between(startTime, now))
        }
        val newSegment = Segment(
            segmentId = 0,
            taskId = taskId,
            startTime = now,
            duration = null,
            type = type
        )
        taskRepository.updateSegmentAndInsertNew(
            existing = lastSegment,
            new = newSegment
        )
    }



    private fun startIncrementer(sessionTimer: SessionTimer) {
        acquireWakeLock()
        timer = sessionTimer
    }

    private fun stopIncrementer() {
        releaseWakeLock()
        timer = null
    }

    private fun SessionTimer.setRunning() {
        setWorkIncrementer()
        setState(State.RUNNING)
    }

    private fun SessionTimer.setPaused() {
        setBreakIncrementer()
        setState(State.PAUSED)
    }

    private fun setSuspended() {
        setState(State.CLOSING)
        notificationManager?.cancelNotification()
        stopIncrementer()
        _loadedTask = null
        _loadedTaskId.update { null }
    }

    private fun setPreparing(taskId: Long) {
        _loadedTaskId.update { taskId }
        setState(State.PREPARING)
    }


    private fun start(taskId: Long) {
        when (_internalState.value) {
            State.DORMANT -> prepareAndStart(taskId)
            State.INIT,
            State.PREPARING,
            State.CLOSING -> error(
                "timer.start() must not be called in ${_state.value}"
            )
            State.PAUSED,
            State.RUNNING -> suspend(taskId)
        }
    }


    private fun resume() {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.RUNNING,
            State.CLOSING -> error("timer.resume() must not be called in ${_state.value}")
            State.PAUSED -> {}
        }

        timer?.setRunning()
            ?: error("timer is null")

        coroutineScope.launch {
            _loadedTask!!.value.endLastSegmentAndStartNew(Segment.Type.WORK)
        }
    }


    private fun pause() {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.PAUSED,
            State.CLOSING -> error("timer.pause() must not be called in ${_state.value}")
            State.RUNNING -> {}
        }

        timer?.setPaused()
            ?: error("timer is null")

        coroutineScope.launch {
            _loadedTask!!.value.endLastSegmentAndStartNew(Segment.Type.BREAK)
        }
    }


    private fun addMarker() {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.PAUSED,
            State.CLOSING -> error("timer.pause() must not be called in ${_state.value}")
            State.RUNNING -> {}
        }

        coroutineScope.launch {
            addMarkerUseCase(
                sessionRepository = taskRepository,
                session = _loadedTask?.value ?: error ("loaded task is null")
            )
        }
    }


    private fun suspend(replaceWith: Long? = null) {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.CLOSING -> error(
                "timer.suspend() must not be called in ${_state.value}"
            )
            State.RUNNING,
            State.PAUSED -> { }
        }

        val session = _loadedTask?.value ?: error ("loaded task must not be null")
        setSuspended()

        coroutineScope.launch {
            session.endLastSegmentAndStartNew(Segment.Type.SUSPEND)
        }

        replaceWith?.let{ replacement ->
            prepareAndStart(replacement)
        } ?: stop()
    }

    private fun finish() {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.CLOSING -> error(
                "timer.finish() must not be called in ${_state.value}"
            )
            State.RUNNING,
            State.PAUSED -> { }
        }

        val session = _loadedTask?.value ?: error ("loaded task must not be null")
        setSuspended()

        coroutineScope.launch {
            session.complete(taskRepository)
        }

        stop()
    }
}