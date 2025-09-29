package com.wordco.clockworkandroid.timer_feature.ui.timer

import android.Manifest
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.Marker
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.ui.timer.Second
import com.wordco.clockworkandroid.core.ui.timer.TimerState
import com.wordco.clockworkandroid.timer_feature.ui.util.complete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
    private var notificationManager: TimerNotificationManager? = null
    private lateinit var taskRepository: TaskRepository


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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = notificationManager?.buildPreparingNotification()
            ?: return START_NOT_STICKY

        startForeground(TimerNotificationManager.NOTIFICATION_ID, notification)

        when (intent?.action) {
            "ACTION_RESUME" -> resume()
            "ACTION_PAUSE" -> pause()
            "ACTION_MARKER" -> addMarker()
        }

        return START_STICKY
    }

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }


    private fun loadElapsedTimes(
        workTime: Duration,
        breakTime: Duration,
        lastSegment: Segment
    ) : Pair<Second, Int>{
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


    fun prepareAndStartActiveSession(taskId: Long) {
        coroutineScope.launch {
            taskRepository.getTask(taskId).let { flow ->
                val task = flow.first() as? StartedTask
                    ?: error("Attempted to load new or completed session as started")

                val (workSeconds, breakMinutes) = loadElapsedTimes(
                    workTime = task.workTime,
                    breakTime = task.breakTime,
                    lastSegment = task.segments.last()
                )

                when (task.status()) {
                    StartedTask.Status.SUSPENDED -> error(
                        "Attempted to restore an unrestorable task on load $task"
                    )
                    StartedTask.Status.RUNNING -> {
                        timer = SessionTimer(
                            coroutineScope = coroutineScope,
                            initialWorkSeconds = workSeconds,
                            initialBreakMinutes = breakMinutes,
                        )
                        _loadedTask = flow.map { it as StartedTask }
                            .stateIn(
                                scope = coroutineScope,
                                started = SharingStarted.WhileSubscribed(),
                                initialValue = task
                            )
                        setState(State.RUNNING)
                    }
                    StartedTask.Status.PAUSED -> {
                        timer = SessionTimer(
                            coroutineScope = coroutineScope,
                            initialWorkSeconds = workSeconds,
                            initialBreakMinutes = breakMinutes,
                            startAsBreak = true
                        )
                        _loadedTask = flow.map { it as StartedTask }
                            .stateIn(
                                scope = coroutineScope,
                                started = SharingStarted.WhileSubscribed(),
                                initialValue = task
                            )
                        setState(State.PAUSED)
                    }
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
        }.catch {
            throw it
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
        }.catch {
            throw it
        }.collect { state ->
            _state.update { state }
        }
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


    /*
        TASK REGISTRY UTILITIES
     */
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

    private fun setPreparing(taskId: Long) {
        _loadedTaskId.update { taskId }
        setState(State.PREPARING)
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
        timer = null
        _loadedTask = null
        _loadedTaskId.update { null }
    }

    fun start(taskId: Long) {
        when (_internalState.value) {
            State.DORMANT -> prepareAndStartInactiveSession(taskId)
            State.INIT,
            State.PREPARING,
            State.CLOSING -> error(
                "timer.start() must not be called in ${_state.value}"
            )
            State.PAUSED,
            State.RUNNING -> suspend(taskId)
        }
    }

    private fun prepareAndStartInactiveSession(taskId: Long) {
        coroutineScope.launch {
            setPreparing(taskId)
            val taskFlow = taskRepository.getTask(taskId)
            val task = taskFlow.first()

            val (workTime, breakTime) = when (task) {
                is CompletedTask -> error("Completed tasks may not be loaded into the timer")
                is NewTask -> {
                    task.start()
                    Pair(0,0)
                }
                is StartedTask -> {
                    loadElapsedTimes(
                        workTime = task.workTime,
                        breakTime = task.breakTime,
                        lastSegment = task.segments.last()
                    ).also {
                        task.endLastSegmentAndStartNew(Segment.Type.WORK)
                    }
                }
            }
            _loadedTask = taskFlow.map { it as StartedTask }.run {
                stateIn(
                    coroutineScope,
                    SharingStarted.WhileSubscribed(),
                    first()
                )
            }
            timer = SessionTimer(
                coroutineScope,
                workTime,
                breakTime
            )
            setState(State.RUNNING)
        }
    }

    private suspend fun NewTask.start() {
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
    }


    fun resume() {
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


    fun pause() {
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

    fun addMarker() : String {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.PAUSED,
            State.CLOSING -> error("timer.pause() must not be called in ${_state.value}")
            State.RUNNING -> {}
        }

        val now = Instant.now()

        return _loadedTask?.value?.let { session ->
            "Marker ${session.markers.size + 1}".also { name ->
                coroutineScope.launch {
                    taskRepository.insertMarker(
                        Marker(
                            markerId = 0,
                            taskId = session.taskId,
                            startTime = now,
                            label = name
                        )
                    )
                }
            }
        } ?: error ("loaded task is null")
    }


    fun suspend(replaceWith: Long? = null) {
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
            prepareAndStartInactiveSession(replacement)
        } ?: setState(State.DORMANT)
    }

    fun finish() {
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

        setState(State.DORMANT)
    }
}