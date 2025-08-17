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

    data object Idle : Empty

    // TODO: use this to show when the timer is preparing to start a task
    data object Preparing : Empty

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
        IDLE, RETRIEVING, READY, RUNNING, PAUSED
    }

    private val _internalState = MutableStateFlow(State.IDLE)

    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)

    private var _loadedTask: StateFlow<Task?> = MutableStateFlow<Task?>(null)

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
                    State.IDLE -> TimerState.Idle
                    State.RETRIEVING, State.READY -> TimerState.Preparing
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


    // FIXME: clean up
    private fun load(flowSupplier: () -> StateFlow<Task?>) {
        if (_internalState.value != State.IDLE) {
            suspend()
        }

        cancelCollection()

        _loadedTask = flowSupplier()

        initCollection()
    }


    // FIXME: clean up
    fun start(taskFlow: StateFlow<Task?>) {
        coroutineScope.launch {
            load {
                _internalState.update { State.RETRIEVING }
                taskFlow.stateIn(
                    coroutineScope,
                    SharingStarted.WhileSubscribed(),
                    taskFlow.value
                )
            }
            _elapsedSeconds.update {
                _loadedTask.first { it != null }!!.workTime.seconds.toInt()
            }
            _internalState.update { State.READY }
            resume()
        }
    }

    fun resume() {
        when (_internalState.value) {
            State.IDLE, State.RETRIEVING, State.RUNNING -> return
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
            State.IDLE, State.RETRIEVING, State.PAUSED, State.READY -> return
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

    fun suspend() {
        when (_internalState.value) {
            State.IDLE, State.RETRIEVING -> return
            State.RUNNING -> {
                workTimeIncJob!!.cancel()
                workTimeIncJob = null
            }
            State.PAUSED -> {
                // TODO: Stop break time incrementer
            }
            State.READY -> {}
        }

        _internalState.update { State.IDLE }

        coroutineScope.launch {
            _loadedTask.value!!.run {
                taskRepository.updateTask(this.copy(status = ExecutionStatus.SUSPENDED))

                // Check if this is a new task before updating the last segment
                // Catch case of suspend on READY on NEW task
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
                        type = SegmentType.SUSPEND
                    )
                )
            }
        }

        load { MutableStateFlow(null) }
    }

}