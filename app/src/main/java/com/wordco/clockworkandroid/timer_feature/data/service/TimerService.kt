package com.wordco.clockworkandroid.timer_feature.data.service

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
import com.wordco.clockworkandroid.timer_feature.domain.repository.TimerNotificationManager
import com.wordco.clockworkandroid.timer_feature.domain.use_case.AddMarkerUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.CompleteStartedSessionUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.EndLastSegmentAndStartNewUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.StartNewSessionUseCase
import com.wordco.clockworkandroid.timer_feature.ui.notification.TimerNotificationManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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



    private sealed interface InternalState {
        data object Dormant : InternalState
        data object Closing : InternalState
        data class Preparing(
            val sessionId: Long,
        ) : InternalState
        data class Active(
            val session: StateFlow<StartedTask>,
            val timer: SessionTimer,
        ) : InternalState
    }

    private val internalState = MutableStateFlow<InternalState>(InternalState.Dormant)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = internalState.flatMapLatest { serviceState ->
        when (serviceState) {
            is InternalState.Active -> {
                combine(
                    serviceState.session,
                    serviceState.timer.state,
                ) { session, timerState ->
                    when (timerState) {
                        is SessionTimer.State.Break -> TimerState.Paused(
                            taskId = session.taskId,
                            elapsedWorkSeconds = timerState.elapsedWorkSeconds,
                            elapsedBreakMinutes = timerState.elapsedBreakSeconds / 60
                        )

                        is SessionTimer.State.Work -> TimerState.Running(
                            taskId = session.taskId,
                            elapsedWorkSeconds = timerState.elapsedWorkSeconds,
                            elapsedBreakMinutes = timerState.elapsedBreakSeconds / 60
                        )
                    }
                }
            }

            InternalState.Closing -> flowOf(TimerState.Closing)
            InternalState.Dormant -> flowOf(TimerState.Dormant)
            is InternalState.Preparing -> flowOf(
                TimerState.Preparing(serviceState.sessionId)
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        val appContainer = (application as MainApplication).appContainer

        taskRepository = appContainer.sessionRepository
        addMarkerUseCase = appContainer.addMarkerUseCase
        endLastSegmentAndStartNewUseCase = appContainer.endLastSegmentAndStartNewUseCase
        startNewSessionUseCase = appContainer.startNewSessionUseCase
        completeStartedSessionUseCase = appContainer.completeStartedSessionUseCase

        notificationManager = appContainer.timerNotificationManager

        setState(InternalState.Dormant)

        notificationManager.observeState(state)
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
        private const val PREFIX = "com.wordco.clockworkandroid.timer"
        const val ACTION_START = "$PREFIX.ACTION_START"
        const val ACTION_PAUSE = "$PREFIX.ACTION_PAUSE"
        const val ACTION_RESUME = "$PREFIX.ACTION_RESUME"
        const val ACTION_SUSPEND = "$PREFIX.ACTION_SUSPEND"
        const val ACTION_FINISH = "$PREFIX.ACTION_FINISH"
        const val ACTION_STOP_SERVICE = "$PREFIX.ACTION_STOP_SERVICE"
        const val EXTRA_TASK_ID = "$PREFIX.EXTRA_TASK_ID"
        const val ACTION_MARKER = "$PREFIX.ACTION_MARKER"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = notificationManager.getForegroundNotification()

        startForeground(TimerNotificationManagerImpl.Companion.NOTIFICATION_ID, notification)

        when (intent?.action) {
            // android uses a null intent when restarting the service after killing it
            null -> {
                coroutineScope.launch {
                    taskRepository.getActiveTaskId()?.let {
                        start(it)
                    }
                }
            }
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
        setState(InternalState.Dormant)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun setState(newInternalState: InternalState) {
        internalState.update { currentState ->
            // if the new state does not use the existing timer (if there is one)
            //  stop it
            if (currentState is InternalState.Active && !(
                newInternalState is InternalState.Active &&
                newInternalState.timer === currentState.timer
            )) {
                currentState.timer.stop()
            }

            newInternalState
        }
    }


    private suspend fun prepareAndStart(taskId: Long) {
        setState(InternalState.Preparing(
            sessionId = taskId
        ))

        val stateFlow = createStartedSessionStateFlow(taskId)

        val sessionTimer = SessionTimer(
            coroutineScope = coroutineScope,
            session = stateFlow
        )

        setState(InternalState.Active(
            session = stateFlow,
            timer = sessionTimer
        ))
    }

    private suspend fun createStartedSessionStateFlow(taskId: Long) : StateFlow<StartedTask> {
        val flow = taskRepository.getTask(taskId)
        val session = flow.first()

        return when (session) {
            is CompletedTask -> error("Attempted to load completed session")
            is NewTask -> {
                val session = startNewSessionUseCase(session)
                flow.map { it as StartedTask }
                    .stateIn(
                        scope = coroutineScope,
                        started = SharingStarted.Companion.WhileSubscribed(),
                        initialValue = session
                    )
            }

            is StartedTask -> {
                when (session.status()) {
                    StartedTask.Status.SUSPENDED -> {
                        endLastSegmentAndStartNewUseCase(
                            session,
                            Segment.Type.WORK
                        )
                        flow.map { it as StartedTask }.run {
                            stateIn(
                                scope = coroutineScope,
                                started = SharingStarted.Companion.WhileSubscribed(),
                                initialValue = first()
                            )
                        }
                    }

                    StartedTask.Status.PAUSED,
                    StartedTask.Status.RUNNING -> {
                        flow.map { it as StartedTask }
                            .stateIn(
                                scope = coroutineScope,
                                started = SharingStarted.Companion.WhileSubscribed(),
                                initialValue = session
                            )
                    }
                }
            }
        }
    }


    private fun start(taskId: Long) {
        when (internalState.value) {
            is InternalState.Active -> suspend(taskId)
            InternalState.Dormant -> coroutineScope.launch {
                prepareAndStart(taskId)
            }
            InternalState.Closing,
            is InternalState.Preparing -> {}
        }
    }


    private fun resume() {
        val internalState = this@TimerService.internalState.value.let {
            (it as? InternalState.Active)
        }

        if (internalState?.timer?.state?.value !is SessionTimer.State.Break) {
            return
        }

        internalState.timer.setWork()

        coroutineScope.launch {
            endLastSegmentAndStartNewUseCase(
                internalState.session.value,
                Segment.Type.WORK
            )
        }
    }


    private fun pause() {
        val internalState = this@TimerService.internalState.value.let {
            (it as? InternalState.Active)
        }

        if (internalState?.timer?.state?.value !is SessionTimer.State.Work) {
            return
        }

        internalState.timer.setBreak()

        coroutineScope.launch {
            endLastSegmentAndStartNewUseCase(
                internalState.session.value,
                Segment.Type.BREAK
            )
        }
    }


    private fun addMarker() {
        val internalState = this@TimerService.internalState.value.let {
            (it as? InternalState.Active)
        }

        if (internalState?.timer?.state?.value !is SessionTimer.State.Work) {
            return
        }

        coroutineScope.launch {
            addMarkerUseCase(
                sessionRepository = taskRepository,
                session = internalState.session.value
            )
        }
    }


    private fun suspend(replaceWith: Long? = null) {
        val session = internalState.value
            .let { it as? InternalState.Active }?.session?.value ?: return

        setState(InternalState.Closing)

        coroutineScope.launch {
            endLastSegmentAndStartNewUseCase(
                session,
                Segment.Type.SUSPEND
            )
        }

        replaceWith?.let{ replacement ->
            coroutineScope.launch {
                prepareAndStart(replacement)
            }
        } ?: stop()
    }


    private fun finish() {
        val session = internalState.value
            .let { it as? InternalState.Active }?.session?.value ?: return

        setState(InternalState.Closing)

        coroutineScope.launch {
            completeStartedSessionUseCase(session)
        }

        stop()
    }
}