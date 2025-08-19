package com.wordco.clockworkandroid.domain.model

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
        val elapsedSeconds: Int
    }

    data class Running(
        override val task: Task,
        override val elapsedSeconds: Int
    ) : HasTask

    data class Paused(
        override val task: Task,
        override val elapsedSeconds: Int
    ) : HasTask
}


class Timer(
    private val dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) {

    private enum class State {
        DORMANT, PREPARING, READY, RUNNING, PAUSED, CLOSING
    }

    private val _internalState = MutableStateFlow(State.DORMANT)

    private val _timerState = MutableStateFlow<TimerState>(TimerState.Dormant)

    private var _loadedTask: StateFlow<Task?> = MutableStateFlow(null)

    private val _elapsedSeconds = MutableStateFlow(0)

    private val coroutineScope = CoroutineScope(dispatcher)

    private var workTimeIncJob: Job? = null
    private var collectionJob: Job? = null


    init {
        initCollection()
    }


    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private fun initCollection() {
        collectionJob = coroutineScope.launch {
            combine (
                _internalState,
                _loadedTask,
                _elapsedSeconds
            ) {
                taskState, loadedTask, elapsedTime ->

                when (taskState) {
                    State.DORMANT -> TimerState.Dormant
                    State.PREPARING, State.READY -> TimerState.Preparing
                    State.CLOSING -> TimerState.Closing
                    State.RUNNING -> TimerState.Running(
                        task = loadedTask!!,
                        elapsedSeconds = elapsedTime
                    )
                    State.PAUSED -> TimerState.Paused(
                        task = loadedTask!!,
                        elapsedSeconds = elapsedTime
                    )
                }
            }.catch {
                throw it
            }.collect {
                _timerState.value = it
            }
        }
    }

    private fun cancelCollection() {
        collectionJob?.cancel()
        collectionJob = null
    }


    fun start(taskFlow: StateFlow<Task?>) {
        val replaceStrategy = ReplaceWith(
            taskFlow,
            ::resume
        )

        when (_internalState.value) {
            State.DORMANT -> replaceStrategy()
            State.PREPARING, State.CLOSING, State.READY -> throw IllegalStateException(
                "timer.start() must not be called in ${_timerState.value}"
            )
            State.PAUSED, State.RUNNING -> suspend(
                replaceStrategy
            )
        }
    }

    fun resume() {
        when (_internalState.value) {
            State.DORMANT, State.PREPARING, State.RUNNING, State.CLOSING -> throw IllegalStateException(
                "timer.resume() must not be called in ${_timerState.value}"
            )
            State.PAUSED -> {
                // TODO: Stop break time incrementer
            }
            State.READY -> {}
        }

        _internalState.update { State.RUNNING }

        workTimeIncJob = coroutineScope.launch {
            while (isActive) {
                delay(1000)
                _elapsedSeconds.update { it + 1 }
            }
        }

        coroutineScope.launch {
            _loadedTask.value!!.run {
                taskRepository.updateTask(this.copy(status = ExecutionStatus.RUNNING))

                // Check if this is a new task before updating the last segment
                this.segments.takeIf { it.isNotEmpty() }?.last()?.run {
                    taskRepository.updateSegment(
                        this.copy(duration = Duration.between(startTime, Instant.now()))
                    )
                }

                taskRepository.insertSegment(
                    Segment(
                        segmentId = 0,
                        taskId = taskId,
                        startTime = Instant.now(),
                        duration = null,
                        type = SegmentType.WORK
                    )
                )
            }
        }
    }

    fun pause() {
        when (_internalState.value) {
            State.DORMANT, State.PREPARING, State.PAUSED, State.CLOSING, State.READY -> throw IllegalStateException(
                "timer.pause() must not be called in ${_timerState.value}"
            )
            State.RUNNING -> {}
        }

        _internalState.update { State.PAUSED }

        workTimeIncJob!!.cancel()
        workTimeIncJob = null

        coroutineScope.launch {
            _loadedTask.value!!.run {
                taskRepository.updateTask(this.copy(status = ExecutionStatus.PAUSED))
                taskRepository.updateSegment(this.segments.last().run {
                    this.copy(duration = Duration.between(startTime, Instant.now()))
                })

                taskRepository.insertSegment(
                    Segment(
                        segmentId = 0,
                        taskId = taskId,
                        startTime = Instant.now(),
                        duration = null,
                        type = SegmentType.BREAK
                    )
                )
            }
        }
    }

    sealed interface SuspendAction {
        operator fun invoke()
    }

    inner class Close : SuspendAction {
        override fun invoke() {
            cancelCollection()
            _timerState.update { TimerState.Dormant }
            _internalState.update { State.DORMANT }
            _loadedTask = MutableStateFlow(null)
            _elapsedSeconds.update { 0 }
        }
    }

    inner class ReplaceWith(
        val taskFlow: StateFlow<Task?>,
        val onReady: () -> Unit
    ) : SuspendAction {
        override fun invoke() {
            cancelCollection()
            _timerState.update { TimerState.Preparing }
            _internalState.update { State.PREPARING }
            _loadedTask = taskFlow.stateIn(
                coroutineScope,
                SharingStarted.WhileSubscribed(),
                taskFlow.value
            )

            initCollection()

            coroutineScope.launch {
                _elapsedSeconds.update {
                    _loadedTask.first { it != null }!!.workTime.seconds.toInt()
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
    }


    fun suspend(suspendAction: SuspendAction) {
        when (_internalState.value) {
            State.DORMANT, State.PREPARING, State.CLOSING, State.READY -> throw IllegalStateException(
                "timer.suspend() must not be called in ${_timerState.value}"
            )
            State.RUNNING -> {
                workTimeIncJob!!.cancel()
                workTimeIncJob = null
            }
            State.PAUSED -> {
                // TODO: Stop break time incrementer
            }
        }

        _internalState.update { State.CLOSING }

        coroutineScope.launch {
            _loadedTask.value!!.run {
                taskRepository.updateTask(this.copy(status = ExecutionStatus.SUSPENDED))
                taskRepository.updateSegment(this.segments.last().run {
                    this.copy(duration = Duration.between(startTime, Instant.now()))
                })

                taskRepository.insertSegment(
                    Segment(
                        segmentId = 0,
                        taskId = taskId,
                        startTime = Instant.now(),
                        duration = null,
                        type = SegmentType.SUSPEND
                    )
                )
            }
        }

        suspendAction()
    }

}