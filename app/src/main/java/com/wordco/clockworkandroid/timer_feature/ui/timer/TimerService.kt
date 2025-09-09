package com.wordco.clockworkandroid.timer_feature.ui.timer

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.ui.timer.TimerState
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
    private lateinit var notificationManager: TimerNotificationManager
    private lateinit var taskRepository: TaskRepository


    private enum class State {
        INIT, DORMANT, PREPARING, RUNNING, PAUSED, CLOSING, FINISHED
    }

    private val _internalState = MutableStateFlow(State.INIT)

    private var _loadedTask: StateFlow<StartedTask?> = MutableStateFlow(null)

    private val _loadedTaskId: MutableStateFlow<Long?> = MutableStateFlow(null)

    private val _elapsedWorkSeconds = MutableStateFlow(0)
    private val _elapsedBreakMinutes = MutableStateFlow(0)

    private var incJob: Job? = null
    private var collectionJob: Job? = null

    private val _state = MutableStateFlow<TimerState>(TimerState.Dormant)
    val state = _state.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        notificationManager = TimerNotificationManager(this)
        taskRepository = (application as MainApplication).taskRepository
        Log.i("TimerService", "Timer Service Created")
        restoreAfterExit()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTION_RESUME" -> resume()
            "ACTION_PAUSE" -> pause()
        }

        return START_NOT_STICKY
    }

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(p0: Intent?): IBinder {
        Log.i("TimerServiceBinder", "onBind Called")
        return binder
    }

    private fun clearTask() {
        _internalState.update { State.DORMANT }
        _elapsedWorkSeconds.update { 0 }
        _elapsedBreakMinutes.update { 0 }
        setLoadedTask(null)
        stopSelf()
    }


    private fun loadElapsedTimes(
        workTime: Duration,
        breakTime: Duration,
        lastSegment: Segment
    ) {
        val sinceStarted = Duration.between(lastSegment.startTime, Instant.now()).seconds

        when (lastSegment.type) {
            Segment.Type.WORK -> {
                _elapsedWorkSeconds.update {
                    workTime.seconds.plus(sinceStarted).toInt()
                }
                _elapsedBreakMinutes.update {
                    breakTime.toMinutes().toInt()
                }
            }
            Segment.Type.BREAK -> {
                _elapsedWorkSeconds.update {
                    workTime.seconds.toInt()
                }
                _elapsedBreakMinutes.update {
                    breakTime.toMinutes().plus(sinceStarted / 60).toInt()
                }
            }
            Segment.Type.SUSPEND -> {
                _elapsedWorkSeconds.update {
                    workTime.seconds.toInt()
                }
                _elapsedBreakMinutes.update {
                    breakTime.toMinutes().toInt()
                }
            }
            //FIXME
            Segment.Type.FINISHED -> { }
        }
    }


    private fun restoreAfterExit() {
        coroutineScope.launch {
            taskRepository.getActiveTask()?.let { flow ->
                val task = flow.stateIn(
                    coroutineScope,
                    SharingStarted.WhileSubscribed(),
                    flow.first()
                )

                setLoadedTask(task)

                loadElapsedTimes(
                    workTime = task.value.workTime,
                    breakTime = task.value.breakTime,
                    lastSegment = task.value.segments.last()
                )

                when (task.value.status()) {
                    StartedTask.Status.SUSPENDED -> error(
                        "Attempted to restore an unrestorable task on load ${task.value}"
                    )
                    StartedTask.Status.RUNNING -> { setRunning() }
                    StartedTask.Status.PAUSED -> { setPaused() }
                    StartedTask.Status.FINISHED -> { setFinished() }
                }
            } ?: _internalState.update { State.DORMANT }
        }
    }


    private fun startCollection() {
        collectionJob = coroutineScope.launch {
            combine (
                _internalState,
                _loadedTask,
                _loadedTaskId,
                _elapsedWorkSeconds,
                _elapsedBreakMinutes
            ) { taskState, loadedTask, taskId, workTime, breakTime ->
                when (taskState) {
                    State.DORMANT -> TimerState.Dormant
                    State.INIT,
                    State.PREPARING -> TimerState.Preparing(taskId?:error(
                        "taskId must be set before timer can enter State.PREPARING"
                    ))
                    State.CLOSING -> TimerState.Closing
                    State.RUNNING -> TimerState.Running(
                        task = loadedTask!!,
                        elapsedWorkSeconds = workTime,
                        elapsedBreakMinutes = breakTime
                    )
                    State.PAUSED -> TimerState.Paused(
                        task = loadedTask!!,
                        elapsedWorkSeconds = workTime,
                        elapsedBreakMinutes = breakTime
                    )
                    State.FINISHED -> TimerState.Finished(
                        task = loadedTask!!,
                        elapsedWorkSeconds = workTime,
                        elapsedBreakMinutes = breakTime
                    )
                }
            }.catch {
                throw it
            }.collect { state ->
                _state.update { state }

                if (state is TimerState.Active) {
                    notificationManager.showNotification(state)
                }
            }
        }
    }


    private fun cancelCollection() {
        collectionJob?.cancel()
        collectionJob = null
    }

    private fun setLoadedTask(taskFlow: StateFlow<StartedTask>?) {
        cancelCollection()
        _loadedTask = taskFlow ?: MutableStateFlow(null)
        startCollection()
    }


    /*
        TIMER JOB UTILITIES
     */
    private val workTimer = Incrementer.of(
        interval = 1000,
        initialOffset = { _loadedTask.value!!.workTime.toMillis() },
        stateField = _elapsedWorkSeconds
    )

    private val breakTimer = Incrementer.of(
        interval = 60000,
        initialOffset = { _loadedTask.value!!.breakTime.toMillis() },
        stateField =  _elapsedBreakMinutes
    )


    private fun setIncrementer(incrementer: Incrementer) {
        incJob?.cancel()
        incJob = coroutineScope.launch (block = incrementer())
    }

    private fun cancelIncrementer() {
        incJob?.cancel()
        incJob = null
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
        _internalState.update { State.PREPARING }
    }

    private fun setRunning() {
        if (_loadedTask.value == null) {
            error("Attempted to enter invalid state: Running without a loaded session")
        }
        _internalState.update { State.RUNNING }

        setIncrementer(workTimer)
    }

    private fun setPaused() {
        if (_loadedTask.value == null) {
            error("Attempted to enter invalid state: Paused without a loaded session")
        }
        _internalState.update { State.PAUSED }

        setIncrementer(breakTimer)
    }

    private fun setSuspended() {
        if (_loadedTask.value == null) {
            error("Attempted to enter invalid state: Suspended without a loaded session")
        }
        _internalState.update { State.CLOSING }

        cancelIncrementer()
        notificationManager.cancelNotification()
    }

    private fun setFinished() {
        if (_loadedTask.value == null) {
            error("Attempted to enter invalid state: Finished without a loaded session")
        }
        _internalState.update { State.FINISHED }

        cancelIncrementer()
    }

    fun start(taskId: Long) {
        when (_internalState.value) {
            State.DORMANT -> prepareAndStart(taskId)
            State.INIT,
            State.PREPARING,
            State.CLOSING,
            State.FINISHED -> error(
                "timer.start() must not be called in ${_state.value}"
            )
            State.PAUSED,
            State.RUNNING -> suspend(
                taskId
            )
        }
    }


    private fun prepareAndStart(taskId: Long) {
        coroutineScope.launch {
            setPreparing(taskId)
            val taskFlow = taskRepository.getTask(taskId)
            val task = taskFlow.first()

            when (task) {
                is CompletedTask -> error("Completed tasks may not be loaded into the timer")
                is NewTask -> task.start()
                is StartedTask -> {
                    loadElapsedTimes(
                        workTime = task.workTime,
                        breakTime = task.breakTime,
                        lastSegment = task.segments.last()
                    )
                    task.endLastSegmentAndStartNew(Segment.Type.WORK)
                }
            }

            setLoadedTask(
                taskFlow.map { it as StartedTask }.run {
                    stateIn(
                        coroutineScope,
                        SharingStarted.WhileSubscribed(),
                        first()
                    )
                }
            )

            setRunning()
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
            name = name,
            dueDate = dueDate,
            difficulty = difficulty,
            color = color,
            userEstimate = userEstimate,
            segments = listOf(segment),
            markers = emptyList()
        )

        taskRepository.insertSegment(segment)
        taskRepository.updateTask(task)

        _elapsedWorkSeconds.update { 0 }
        _elapsedBreakMinutes.update { 0 }
    }


    fun resume() {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.RUNNING,
            State.CLOSING,
            State.FINISHED-> error("timer.resume() must not be called in ${_state.value}")
            State.PAUSED -> {}
        }

        setRunning()

        coroutineScope.launch {
            _loadedTask.value!!.endLastSegmentAndStartNew(Segment.Type.WORK)
        }
    }


    fun pause() {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.PAUSED,
            State.CLOSING,
            State.FINISHED -> error("timer.pause() must not be called in ${_state.value}")
            State.RUNNING -> {}
        }

        setPaused()

        coroutineScope.launch {
            _loadedTask.value!!.endLastSegmentAndStartNew(Segment.Type.BREAK)
        }
    }


    fun suspend(replaceWith: Long? = null) {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.CLOSING,
            State.FINISHED -> error(
                "timer.suspend() must not be called in ${_state.value}"
            )
            State.RUNNING,
            State.PAUSED -> { }
        }

        setSuspended()

        coroutineScope.launch {
            _loadedTask.value!!.endLastSegmentAndStartNew(Segment.Type.SUSPEND)
        }

        replaceWith?.let{ replacement ->
            prepareAndStart(replacement)
        } ?: clearTask()
    }
    //FIXME
    fun finish() {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.CLOSING,
            State.FINISHED -> error(
                "timer.finish() must not be called in ${_state.value}"
            )
            State.RUNNING,
            State.PAUSED -> { }
        }
    }
}