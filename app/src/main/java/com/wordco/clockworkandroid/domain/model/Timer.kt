package com.wordco.clockworkandroid.domain.model

import android.util.Log
import com.wordco.clockworkandroid.data.local.entities.SegmentEntity
import com.wordco.clockworkandroid.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

sealed interface TimerState {
    sealed interface Empty : TimerState

    data object Dormant : Empty

    // TODO: use this to show when the timer is preparing to start a task
    data object Preparing : Empty

    data object Closing : Empty

    sealed interface HasTask : TimerState {
        val task: Task
        val elapsedWorkSeconds: Int
    }

    data class Running(
        override val task: Task,
        override val elapsedWorkSeconds: Int
    ) : HasTask

    data class Paused(
        override val task: Task,
        override val elapsedWorkSeconds: Int
    ) : HasTask
}


class Timer(
    private val dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) {

    private enum class State {
        INIT, DORMANT, PREPARING, READY, RUNNING, PAUSED, CLOSING
    }

    private val _internalState = MutableStateFlow(State.PREPARING)

    private val _timerState = MutableStateFlow<TimerState>(TimerState.Dormant)

    private var _loadedTask: StateFlow<Task?> = MutableStateFlow(null)

    private val _elapsedWorkSeconds = MutableStateFlow(0)
    private val _elapsedBreakMinutes = MutableStateFlow(0)

    private val coroutineScope = CoroutineScope(dispatcher)

    private fun workTimeInc(): suspend CoroutineScope.() -> Unit = buildIncrementer(
        interval = 1000,
        initialElapsed = _loadedTask.value!!.workTime.toMillis()
    ) {
        _elapsedWorkSeconds.update { it + 1 }
    }

    private fun breakTimeInc(): suspend CoroutineScope.() -> Unit = buildIncrementer(
        interval = 60000,
        initialElapsed = _loadedTask.value!!.breakTime.toMillis()
    ) {
        _elapsedBreakMinutes.update { it + 1 }
    }

    private fun buildIncrementer(
        interval: Long,
        initialElapsed: Long,
        onTrigger: () -> Unit,
    ): suspend CoroutineScope.() -> Unit {
        return {
            // Wait for next minute
            delay(interval - (initialElapsed % interval))

            // Start after synchronized with minute
            while (isActive) {
                onTrigger()
                delay(interval)
            }
        }
    }

    private var incJob: Job? = null
    private var collectionJob: Job? = null


    init {
        startCollection()
        restoreAfterExit()
    }


    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private fun startCollection() {
        collectionJob = coroutineScope.launch {
            combine (
                _internalState,
                _loadedTask,
                _elapsedWorkSeconds
            ) {
                taskState, loadedTask, elapsedTime ->

                when (taskState) {
                    State.DORMANT -> TimerState.Dormant
                    State.INIT, State.PREPARING, State.READY -> TimerState.Preparing
                    State.CLOSING -> TimerState.Closing
                    State.RUNNING -> TimerState.Running(
                        task = loadedTask!!,
                        elapsedWorkSeconds = elapsedTime
                    )
                    State.PAUSED -> TimerState.Paused(
                        task = loadedTask!!,
                        elapsedWorkSeconds = elapsedTime
                    )
                }
            }.catch {
                throw it
            }.collect {
                _timerState.value = it
            }
        }
    }

    private fun restoreAfterExit() {
        Log.i("TimerLoad", "Searching for an active task on close")
        coroutineScope.launch {
            if (taskRepository.hasActiveTask()) {
                Log.i("TimerLoad", "Found an active task on close")
                val task = taskRepository.getActiveTask().let {
                    flow ->

                    flow.stateIn(
                        coroutineScope,
                        SharingStarted.WhileSubscribed(),
                        flow.first { it != null }!!
                    )
                }

                loadTask(
                    taskFlow = task,
                    onReady = ::continueAfterExit
                )
            } else {
                Log.i("TimerLoad", "All tasks closed.  Loading Normally")
                _internalState.update { State.DORMANT }
            }
        }.invokeOnCompletion(
            {
                throwable ->
                Log.i("TimerLoad", "Search complete")
            }
        )
    }

    private fun cancelCollection() {
        collectionJob?.cancel()
        collectionJob = null
    }


    fun start(taskFlow: StateFlow<Task?>) {
        when (_internalState.value) {
            State.DORMANT -> loadTask(
                taskFlow = taskFlow,
                onReady = ::resume
            )
            State.INIT, State.PREPARING, State.CLOSING, State.READY -> throw IllegalStateException(
                "timer.start() must not be called in ${_timerState.value}"
            )
            State.PAUSED, State.RUNNING -> suspend(
                taskFlow
            )
        }
    }

    private suspend fun Task.updateTaskStatus(newStatus: ExecutionStatus) {
        taskRepository.updateTask(copy(status = newStatus))
    }

    private suspend fun Task.endLastSegment(predicate: (Task) -> Boolean = {true}) {
        if (predicate(this)) {
            taskRepository.updateSegment(segments.last().run {
                copy(duration = Duration.between(startTime, Instant.now()))
            })
        }
    }

    private suspend fun Task.startNewSegment(type: SegmentType) {
        taskRepository.insertSegment(
            Segment(
                segmentId = 0,
                taskId = taskId,
                startTime = Instant.now(),
                duration = null,
                type = type
            )
        )
    }

    fun resume() {
        when (_internalState.value) {
            State.INIT, State.DORMANT, State.PREPARING, State.RUNNING, State.CLOSING -> throw IllegalStateException(
                "timer.resume() must not be called in ${_timerState.value}"
            )
            State.PAUSED -> {
                // TODO: Stop break time incrementer
            }
            State.READY -> {}
        }

        beginRunning()

        coroutineScope.launch {
            _loadedTask.value!!.run {
                updateTaskStatus(ExecutionStatus.RUNNING)

                // Check if this is a new task before updating the last segment
                endLastSegment { segments.isNotEmpty() }

                startNewSegment(SegmentType.WORK)
            }
        }
    }

    private fun continueAfterExit() {
        when (_loadedTask.value!!.status) {
            ExecutionStatus.NOT_STARTED,
            ExecutionStatus.SUSPENDED,
            ExecutionStatus.COMPLETED -> throw RuntimeException(
                "Attempted to restore an unrestorable task on load"
            )
            ExecutionStatus.RUNNING -> beginRunning()
            ExecutionStatus.PAUSED -> TODO()
        }
    }

    private fun beginRunning() {
        _internalState.update { State.RUNNING }

        incJob = coroutineScope.launch (block = workTimeInc())
    }

    fun pause() {
        when (_internalState.value) {
            State.INIT, State.DORMANT, State.PREPARING, State.PAUSED, State.CLOSING, State.READY -> throw IllegalStateException(
                "timer.pause() must not be called in ${_timerState.value}"
            )
            State.RUNNING -> {}
        }

        _internalState.update { State.PAUSED }

        incJob!!.cancel()
        incJob = null

        coroutineScope.launch {
            _loadedTask.value!!.run {
                updateTaskStatus(ExecutionStatus.PAUSED)
                endLastSegment()
                startNewSegment(SegmentType.BREAK)
            }
        }
    }


    private fun clearTask() {
        cancelCollection()
        _timerState.update { TimerState.Dormant }
        _internalState.update { State.DORMANT }
        _loadedTask = MutableStateFlow(null)
        _elapsedWorkSeconds.update { 0 }
    }

    private fun loadTask(
        taskFlow: StateFlow<Task?>,
        onReady: () -> Unit
    ) {
        cancelCollection()
        _timerState.update { TimerState.Preparing }
        _internalState.update { State.PREPARING }
        _loadedTask = taskFlow.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(),
            taskFlow.value
        )

        startCollection()

        coroutineScope.launch {
            _elapsedWorkSeconds.update {
                _loadedTask.first { it != null }!!.let{
                    task ->

                    // Check if the last segment was a work segment.  If so add it's time
                    val sinceStarted = task.segments.takeIf { it.isNotEmpty() }?.last()
                        ?.takeIf { it.type == SegmentType.WORK }?.duration?.seconds ?: 0

                    task.workTime.seconds.plus(sinceStarted).toInt()
                }
            }
            _internalState.update { State.READY }
        }.invokeOnCompletion {
                cause ->

            if (cause == null) {
                onReady()
            } else {
                throw cause
            }
        }
    }


    fun suspend(replaceWith: StateFlow<Task?>? = null) {
        when (_internalState.value) {
            State.INIT, State.DORMANT, State.PREPARING, State.CLOSING, State.READY -> throw IllegalStateException(
                "timer.suspend() must not be called in ${_timerState.value}"
            )
            State.RUNNING -> {
                incJob!!.cancel()
                incJob = null
            }
            State.PAUSED -> {
                // TODO: Stop break time incrementer
            }
        }

        _internalState.update { State.CLOSING }

        coroutineScope.launch {
            _loadedTask.value!!.run {
                updateTaskStatus(ExecutionStatus.SUSPENDED)
                endLastSegment()
                startNewSegment(SegmentType.SUSPEND)
            }
        }

        replaceWith?.let{
            replacement ->

            loadTask(
                replacement,
                onReady = ::resume
            )
        } ?: clearTask()
    }

}