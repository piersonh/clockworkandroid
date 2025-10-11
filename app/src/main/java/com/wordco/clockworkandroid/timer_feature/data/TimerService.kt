package com.wordco.clockworkandroid.timer_feature.data

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
import com.wordco.clockworkandroid.core.domain.model.Second
import com.wordco.clockworkandroid.core.domain.model.TimerState
import com.wordco.clockworkandroid.timer_feature.domain.model.SegmentTimer
import com.wordco.clockworkandroid.timer_feature.domain.use_case.AddMarkerUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.EndLastSegmentAndStartNewUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.StartNewSessionUseCase
import com.wordco.clockworkandroid.timer_feature.domain.model.SessionTimer
import com.wordco.clockworkandroid.timer_feature.domain.use_case.CompleteStartedSessionUseCase
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
    private lateinit var startNewSessionUseCase: StartNewSessionUseCase
    private lateinit var endLastSegmentAndStartNewUseCase: EndLastSegmentAndStartNewUseCase
    private lateinit var completeStartedSessionUseCase: CompleteStartedSessionUseCase


    private enum class State {
        INIT, DORMANT, PREPARING, RUNNING, PAUSED, CLOSING
    }

    private val _internalState = MutableStateFlow(State.INIT)

    private val _loadedTaskId: MutableStateFlow<Long?> = MutableStateFlow(null)

    private var _loadedTask: StateFlow<StartedTask>? = null
    private var timer: SegmentTimer? = null
    private var collectionJob: Job? = null

    private val _state = MutableStateFlow<TimerState>(TimerState.Dormant)
    val state = _state.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        val appContainer = (application as MainApplication).appContainer

        taskRepository = appContainer.sessionRepository
        addMarkerUseCase = appContainer.addMarkerUseCase
        endLastSegmentAndStartNewUseCase = appContainer.endLastSegmentAndStartNewUseCase
        startNewSessionUseCase = appContainer.startNewSessionUseCase
        completeStartedSessionUseCase = appContainer.completeStartedSessionUseCase

        val permissionRequestSignaller = appContainer.permissionRequestSignal
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

        startForeground(TimerNotificationManager.Companion.NOTIFICATION_ID, notification)

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
        combine(
            _internalState,
            _loadedTask!!,
            timer!!.elapsedSeconds,
        ) { taskState, task, segmentTime ->
            when (taskState) {
                State.RUNNING -> TimerState.Running(
                    taskId = task.taskId,
                    elapsedWorkSeconds = task.workTime.seconds.plus(segmentTime).toInt(),
                    elapsedBreakMinutes = task.breakTime.toMinutes().toInt()
                )

                State.PAUSED -> TimerState.Paused(
                    taskId = task.taskId,
                    elapsedWorkSeconds = task.workTime.seconds.toInt(),
                    elapsedBreakMinutes = task.breakTime
                        .plusSeconds(segmentTime.toLong()).toMinutes().toInt()
                )

                else -> error("timer is not active")
            }
        }.collect { state ->
            _state.update { state }
            notificationManager?.showNotification(
                state,
                _loadedTask!!.value
            )
        }
    }

    private suspend fun collectInactive() {
        combine(
            _internalState,
            _loadedTaskId,
        ) { taskState, taskId, ->
            when (taskState) {
                State.DORMANT -> TimerState.Dormant
                State.INIT,
                State.PREPARING -> TimerState.Preparing(
                    taskId ?: error(
                        "taskId must be set before timer can enter State.PREPARING"
                    )
                )

                State.CLOSING -> TimerState.Closing
                else -> error("timer is active")
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
                    setTimer()
                    val session = startNewSessionUseCase(session)
                    _loadedTask = flow.map { it as StartedTask }
                        .stateIn(
                            scope = coroutineScope,
                            started = SharingStarted.Companion.WhileSubscribed(),
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
                            setTimer()
                            endLastSegmentAndStartNewUseCase(
                                session,
                                Segment.Type.WORK
                            )
                            _loadedTask = flow.map { it as StartedTask }.run {
                                stateIn(
                                    scope = coroutineScope,
                                    started = SharingStarted.Companion.WhileSubscribed(),
                                    initialValue = first()
                                )
                            }

                            setState(State.RUNNING)
                        }

                        StartedTask.Status.RUNNING -> {
                            setTimer(session.segments.maxBy { it.startTime }.startTime)
                            _loadedTask = flow.map { it as StartedTask }
                                .stateIn(
                                    scope = coroutineScope,
                                    started = SharingStarted.Companion.WhileSubscribed(),
                                    initialValue = session
                                )
                            setState(State.RUNNING)
                        }

                        StartedTask.Status.PAUSED -> {
                            setTimer(session.segments.maxBy { it.startTime }.startTime)
                            _loadedTask = flow.map { it as StartedTask }
                                .stateIn(
                                    scope = coroutineScope,
                                    started = SharingStarted.Companion.WhileSubscribed(),
                                    initialValue = session
                                )
                            setState(State.PAUSED)
                        }
                    }
                }
            }
        }
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

    private fun setTimer(startTime: Instant? = null) : Int? {
        timer?.stop()
        return timer?.elapsedSeconds?.value.also {
            timer = SegmentTimer(
                coroutineScope,
                startTime?.toEpochMilli() ?: System.currentTimeMillis()
            )
        }
    }


//    private fun startIncrementer(sessionTimer: SessionTimer) {
//        acquireWakeLock()
//        timer = sessionTimer
//    }
//
//    private fun stopIncrementer() {
//        releaseWakeLock()
//        timer = null
//    }

    private fun setRunning() {
        setTimer()
        setState(State.RUNNING)
    }

    private fun setPaused() {
        setTimer()
        setState(State.PAUSED)
    }

    private fun setSuspended() {
        setState(State.CLOSING)
        notificationManager?.cancelNotification()
        timer?.stop()
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

        setRunning()

        coroutineScope.launch {
            endLastSegmentAndStartNewUseCase(
                _loadedTask!!.value,
                Segment.Type.WORK
            )
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

        setPaused()

        coroutineScope.launch {
            endLastSegmentAndStartNewUseCase(
                _loadedTask!!.value,
                Segment.Type.BREAK
            )
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
            endLastSegmentAndStartNewUseCase(
                session,
                Segment.Type.SUSPEND
            )
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
            completeStartedSessionUseCase(session)
        }

        stop()
    }
}