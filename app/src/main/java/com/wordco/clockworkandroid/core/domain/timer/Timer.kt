package com.wordco.clockworkandroid.core.domain.timer

import com.wordco.clockworkandroid.core.data.repository.TaskRepository
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.ExecutionStatus
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.SegmentType
import com.wordco.clockworkandroid.core.domain.model.StartedTask
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
import kotlinx.coroutines.flow.map
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
        val task: StartedTask
        val elapsedWorkSeconds: Int
        val elapsedBreakMinutes: Int
    }

    data class Running(
        override val task: StartedTask,
        override val elapsedWorkSeconds: Int,
        override val elapsedBreakMinutes: Int
    ) : HasTask

    data class Paused(
        override val task: StartedTask,
        override val elapsedWorkSeconds: Int,
        override val elapsedBreakMinutes: Int
    ) : HasTask
}


class Timer(
    private val dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) {

    private val _timerState = MutableStateFlow<TimerState>(TimerState.Dormant)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()


    private enum class State {
        INIT, DORMANT, PREPARING, READY, RUNNING, PAUSED, CLOSING
    }

    private val _internalState = MutableStateFlow(State.PREPARING)

    private var _loadedTask: StateFlow<StartedTask?> = MutableStateFlow(null)

    private val _elapsedWorkSeconds = MutableStateFlow(0)
    private val _elapsedBreakMinutes = MutableStateFlow(0)

    private val coroutineScope = CoroutineScope(dispatcher)

    private var incJob: Job? = null
    private var collectionJob: Job? = null

//    private var dbWriteJob: Job? = null
//    private val dbWritesBuffer = mutableListOf<suspend CoroutineScope.() -> Unit>()


    init {
        restoreAfterExit()
    }


    private fun clearTask() {
        cancelCollection()
        _timerState.update { TimerState.Dormant }
        _internalState.update { State.DORMANT }
        _loadedTask = MutableStateFlow(null)
        _elapsedWorkSeconds.update { 0 }
        _elapsedBreakMinutes.update { 0 }
    }


    private fun loadElapsedTimes(
        workTime: Duration,
        breakTime: Duration,
        lastSegment: Segment
    ) {
        val sinceStarted = Duration.between(lastSegment.startTime, Instant.now()).seconds

        when (lastSegment.type) {
            SegmentType.WORK -> {
                _elapsedWorkSeconds.update {
                    workTime.seconds.plus(sinceStarted).toInt()
                }
                _elapsedBreakMinutes.update {
                    breakTime.toMinutes().toInt()
                }
            }
            SegmentType.BREAK -> {
                _elapsedWorkSeconds.update {
                    workTime.seconds.toInt()
                }
                _elapsedBreakMinutes.update {
                    breakTime.toMinutes().plus(sinceStarted / 60).toInt()
                }
            }
            SegmentType.SUSPEND -> {
                _elapsedWorkSeconds.update {
                    workTime.seconds.toInt()
                }
                _elapsedBreakMinutes.update {
                    breakTime.toMinutes().toInt()
                }
            }
        }
    }


    private fun restoreAfterExit() {
        coroutineScope.launch {
            taskRepository.getActiveTask()?.let { flow ->
                _internalState.update { State.PREPARING }
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
                    ExecutionStatus.NOT_STARTED,
                    ExecutionStatus.SUSPENDED,
                    ExecutionStatus.COMPLETED -> error(
                        "Attempted to restore an unrestorable task on load ${task.value}"
                    )
                    ExecutionStatus.RUNNING -> { setRunning() }
                    ExecutionStatus.PAUSED -> { setPaused() }
                }
            } ?: _internalState.update { State.DORMANT }
//            if (taskRepository.hasActiveTask()) {
//                _internalState.update { State.PREPARING }
//                val task = taskRepository.getActiveTask().let { flow ->
//                    flow.stateIn(
//                        coroutineScope,
//                        SharingStarted.WhileSubscribed(),
//                        flow.first()
//                    )
//                }
//
//                setLoadedTask(task)
//
//                loadElapsedTimes(
//                    workTime = task.value.workTime,
//                    breakTime = task.value.breakTime,
//                    lastSegment = task.value.segments.last()
//                )
//
//                when (task.value.status()) {
//                    ExecutionStatus.NOT_STARTED,
//                    ExecutionStatus.SUSPENDED,
//                    ExecutionStatus.COMPLETED -> error(
//                        "Attempted to restore an unrestorable task on load ${task.value}"
//                    )
//                    ExecutionStatus.RUNNING -> { setRunning() }
//                    ExecutionStatus.PAUSED -> { setPaused() }
//                }
//            } else {
//                _internalState.update { State.DORMANT }
//            }
        }
    }


    private fun startCollection() {
        collectionJob = coroutineScope.launch {
            combine (
                _internalState,
                _loadedTask,
                _elapsedWorkSeconds,
                _elapsedBreakMinutes
            ) { taskState, loadedTask, workTime, breakTime ->
                when (taskState) {
                    State.DORMANT -> TimerState.Dormant
                    State.INIT,
                    State.PREPARING,
                    State.READY -> TimerState.Preparing
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
                }
            }.catch {
                throw it
            }.collect { state ->
                _timerState.update { state }
            }
        }
    }


    private fun cancelCollection() {
        collectionJob?.cancel()
        collectionJob = null
    }

    private fun setLoadedTask(taskFlow: StateFlow<StartedTask?>) {
        cancelCollection()
        _loadedTask = taskFlow
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
     private suspend fun StartedTask.endLastSegmentAndStartNew(type: SegmentType) {
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

//    private fun enqueueDBWrite(writeOp: suspend CoroutineScope.() -> Unit) {
//        Log.i("DBWrite", "${dbWriteJob?.isActive?:false}")
//        if (dbWriteJob?.isActive?:true) {
//            dbWriteJob = coroutineScope.launch {
//                writeOp()
//
//                while (dbWritesBuffer.isNotEmpty()) {
//                    dbWritesBuffer.removeFirstOrNull()!!()
//                }
//            }
//            dbWriteJob?.invokeOnCompletion{ throwable ->
//                dbWriteJob = null
//            }
//        } else {
//            dbWritesBuffer.add(writeOp)
//        }
//    }



    /*
        TIMER EXECUTION LOGIC
     */
    private fun setRunning() {
        _internalState.update { State.RUNNING }

        setIncrementer(workTimer)
    }

    private fun setPaused() {
        _internalState.update { State.PAUSED }

        setIncrementer(breakTimer)
    }

    private fun setSuspended() {
        _internalState.update { State.CLOSING }

        cancelIncrementer()
    }

    fun start(taskId: Long) {
        when (_internalState.value) {
            State.DORMANT -> prepareAndStart(taskId)
            State.INIT,
            State.PREPARING,
            State.CLOSING,
            State.READY -> throw IllegalStateException(
                "timer.start() must not be called in ${_timerState.value}"
            )
            State.PAUSED,
            State.RUNNING -> suspend(
                taskId
            )
        }
    }


    private fun prepareAndStart(taskId: Long) {
        coroutineScope.launch {
            _internalState.update { State.PREPARING }
            val taskFlow = taskRepository.getTask(taskId)
            val task = taskFlow.first()

            val startedTask = when (task) {
                is CompletedTask -> error("Completed tasks may not be loaded into the timer")
                is NewTask -> task.start()
                is StartedTask -> {
                    loadElapsedTimes(
                        workTime = task.workTime,
                        breakTime = task.breakTime,
                        lastSegment = task.segments.last()
                    )
                    task.endLastSegmentAndStartNew(SegmentType.WORK)
                    task
                }
            }

            setLoadedTask(
                taskFlow.map { it as StartedTask }.run {
                    stateIn(
                        coroutineScope,
                        SharingStarted.WhileSubscribed(),
                        startedTask
                    )
                }
            )

            setRunning()
        }
    }

    private suspend fun NewTask.start() : StartedTask {
        val segment = Segment(
            segmentId = 0,
            taskId = taskId,
            startTime = Instant.now(),
            duration = null,
            type = SegmentType.WORK
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

        return task
    }


    fun resume() {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.RUNNING,
            State.CLOSING -> error("timer.resume() must not be called in ${_timerState.value}")
            State.PAUSED,
            State.READY -> {}
        }

        setRunning()

        coroutineScope.launch {
            _loadedTask.value!!.endLastSegmentAndStartNew(SegmentType.WORK)
        }
    }


    fun pause() {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.PAUSED,
            State.CLOSING,
            State.READY -> error("timer.pause() must not be called in ${_timerState.value}")
            State.RUNNING -> {}
        }

        setPaused()

        coroutineScope.launch {
            _loadedTask.value!!.endLastSegmentAndStartNew(SegmentType.BREAK)
        }
    }


    fun suspend(replaceWith: Long? = null) {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.CLOSING,
            State.READY -> throw IllegalStateException(
                "timer.suspend() must not be called in ${_timerState.value}"
            )
            State.RUNNING,
            State.PAUSED -> { }
        }

        setSuspended()

        coroutineScope.launch {
            _loadedTask.value!!.endLastSegmentAndStartNew(SegmentType.SUSPEND)
        }

        replaceWith?.let{ replacement ->
            prepareAndStart(replacement)
        } ?: clearTask()
    }
}


private fun interface Incrementer {
    operator fun invoke(): suspend CoroutineScope.() -> Unit

    companion object {
        fun of(
            interval: Long,
            initialOffset: () -> Long,
            stateField: MutableStateFlow<Int>
        ) : Incrementer = Incrementer {
            runOnInterval(
                interval = interval,
                initialOffset = initialOffset
            ) {
                stateField.update { it + 1 }
            }
        }

        /**
         * Invokes the provided function [block] every [interval] milliseconds
         *
         * @param interval time in milliseconds between triggers
         * @param initialOffset time in milliseconds to offset first interval
         */
        private fun runOnInterval(
            interval: Long,
            initialOffset: () -> Long,
            block: () -> Unit,
        ): suspend CoroutineScope.() -> Unit {
            return {
                // Wait for next minute
                delay(interval - (initialOffset() % interval))

                // Start after synchronized with minute
                while (isActive) {
                    block()
                    delay(interval)
                }
            }
        }
    }
}