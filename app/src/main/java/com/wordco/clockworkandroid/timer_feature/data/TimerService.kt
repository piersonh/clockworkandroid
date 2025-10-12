package com.wordco.clockworkandroid.timer_feature.data

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.TimerState
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.timer_feature.domain.model.SessionTimer
import com.wordco.clockworkandroid.timer_feature.domain.use_case.AddMarkerUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.CompleteStartedSessionUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.EndLastSegmentAndStartNewUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.StartNewSessionUseCase
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

class TimerService() : Service() {

    private val binder = TimerBinder()
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var wakeLock: PowerManager.WakeLock? = null

    private lateinit var notificationManager: TimerNotificationManager
    private lateinit var taskRepository: TaskRepository
    private lateinit var addMarkerUseCase: AddMarkerUseCase
    private lateinit var startNewSessionUseCase: StartNewSessionUseCase
    private lateinit var endLastSegmentAndStartNewUseCase: EndLastSegmentAndStartNewUseCase
    private lateinit var completeStartedSessionUseCase: CompleteStartedSessionUseCase


    private enum class State {
        INIT, DORMANT, PREPARING, ACTIVE, CLOSING
    }

    private val internalState = MutableStateFlow(State.INIT)

    private val loadedTaskId: MutableStateFlow<Long?> = MutableStateFlow(null)

    private var loadedTask: StateFlow<StartedTask>? = null
    private var timer: SessionTimer? = null
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

        notificationManager = TimerNotificationManager(
            this@TimerService,
            appContainer.permissionRequestSignal,
            coroutineScope
        )

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
        val notification = notificationManager.buildPreparingNotification()

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
                internalState.update { state }
                collectionJob = coroutineScope.launch {
                    collectInactive()
                }
            }
            State.ACTIVE -> {
                collectionJob?.cancel()
                internalState.update { state }
                collectionJob = coroutineScope.launch {
                    collectActive()
                }
            }
        }
    }


    private suspend fun collectActive() {
        combine(
            timer!!.state,
            loadedTask!!,
            timer!!.elapsedWorkSeconds,
            timer!!.elapsedBreakSeconds,
        ) { taskState, task, workSeconds, breakSeconds ->
            when (taskState) {
                SessionTimer.State.WORK -> TimerState.Running(
                    taskId = task.taskId,
                    elapsedWorkSeconds = workSeconds,
                    elapsedBreakMinutes = breakSeconds / 60
                )

                SessionTimer.State.BREAK -> TimerState.Paused(
                    taskId = task.taskId,
                    elapsedWorkSeconds = workSeconds,
                    elapsedBreakMinutes = breakSeconds / 60
                )
            }
        }.collect { state ->
            _state.update { state }
            notificationManager.showNotification(
                state,
                loadedTask!!.value
            )
        }
    }

    private suspend fun collectInactive() {
        combine(
            internalState,
            loadedTaskId,
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
                    val session = startNewSessionUseCase(session)
                    loadedTask = flow.map { it as StartedTask }
                        .stateIn(
                            scope = coroutineScope,
                            started = SharingStarted.Companion.WhileSubscribed(),
                            initialValue = session
                        )
                    setTimer(loadedTask!!)
                }

                is StartedTask -> {
                    when (session.status()) {
                        StartedTask.Status.SUSPENDED -> {
                            endLastSegmentAndStartNewUseCase(
                                session,
                                Segment.Type.WORK
                            )
                            loadedTask = flow.map { it as StartedTask }.run {
                                stateIn(
                                    scope = coroutineScope,
                                    started = SharingStarted.Companion.WhileSubscribed(),
                                    initialValue = first()
                                )
                            }
                            setTimer(loadedTask!!)
                        }

                        StartedTask.Status.RUNNING -> {
                            loadedTask = flow.map { it as StartedTask }
                                .stateIn(
                                    scope = coroutineScope,
                                    started = SharingStarted.Companion.WhileSubscribed(),
                                    initialValue = session
                                )
                            setTimer(loadedTask!!)
                        }

                        StartedTask.Status.PAUSED -> {
                            loadedTask = flow.map { it as StartedTask }
                                .stateIn(
                                    scope = coroutineScope,
                                    started = SharingStarted.Companion.WhileSubscribed(),
                                    initialValue = session
                                )
                            setTimer(loadedTask!!)
                        }
                    }
                }
            }
            setState(State.ACTIVE)
        }
    }


    private fun setTimer(session: StateFlow<StartedTask>) {
        timer?.stop()
        timer = SessionTimer(
            coroutineScope = coroutineScope,
            session = session
        )
    }


    private fun setSuspended() {
        setState(State.CLOSING)
        notificationManager.cancelNotification()
        timer?.stop()
        loadedTask = null
        loadedTaskId.update { null }
    }

    private fun setPreparing(taskId: Long) {
        loadedTaskId.update { taskId }
        setState(State.PREPARING)
    }


    private fun start(taskId: Long) {
        when (internalState.value) {
            State.DORMANT -> prepareAndStart(taskId)
            State.INIT,
            State.PREPARING,
            State.CLOSING -> error(
                "timer.start() must not be called in ${_state.value}"
            )
            State.ACTIVE -> suspend(taskId)
        }
    }


    private fun resume() {
        if (timer?.state?.value != SessionTimer.State.BREAK) return

        timer!!.setWork()

        coroutineScope.launch {
            endLastSegmentAndStartNewUseCase(
                loadedTask!!.value,
                Segment.Type.WORK
            )
        }
    }


    private fun pause() {
        if (timer?.state?.value != SessionTimer.State.WORK) return

        timer!!.setBreak()

        coroutineScope.launch {
            endLastSegmentAndStartNewUseCase(
                loadedTask!!.value,
                Segment.Type.BREAK
            )
        }
    }


    private fun addMarker() {
        if (timer?.state?.value != SessionTimer.State.WORK) return

        coroutineScope.launch {
            addMarkerUseCase(
                sessionRepository = taskRepository,
                session = loadedTask?.value ?: error ("loaded task is null")
            )
        }
    }


    private fun suspend(replaceWith: Long? = null) {
        if (internalState.value != State.ACTIVE) return

        val session = loadedTask?.value ?: error ("loaded task must not be null")
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
        if (internalState.value != State.ACTIVE) return

        val session = loadedTask?.value ?: error ("loaded task must not be null")
        setSuspended()

        coroutineScope.launch {
            completeStartedSessionUseCase(session)
        }

        stop()
    }
}