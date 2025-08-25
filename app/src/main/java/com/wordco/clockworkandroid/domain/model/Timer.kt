package com.wordco.clockworkandroid.domain.model

import android.util.Log
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
        val elapsedBreakMinutes: Int
    }

    data class Running(
        override val task: Task,
        override val elapsedWorkSeconds: Int,
        override val elapsedBreakMinutes: Int
    ) : HasTask

    data class Paused(
        override val task: Task,
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

    private var _loadedTask: StateFlow<Task?> = MutableStateFlow(null)

    private val _elapsedWorkSeconds = MutableStateFlow(0)
    private val _elapsedBreakMinutes = MutableStateFlow(0)

    private val coroutineScope = CoroutineScope(dispatcher)

    private var incJob: Job? = null
    private var collectionJob: Job? = null

    private var dbWriteJob: Job? = null

    private val dbWritesBuffer = mutableListOf<suspend CoroutineScope.() -> Unit>()


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


    private fun loadTask(
        taskFlow: StateFlow<Task?>,
        onReady: () -> Unit
    ) {
        cancelCollection()
        _timerState.update { TimerState.Preparing }
        _internalState.update { State.PREPARING }

        coroutineScope.launch {
            _loadedTask = taskFlow.stateIn(
                coroutineScope,
                SharingStarted.WhileSubscribed(),
                taskFlow.first { it != null }
            )

            _loadedTask.value!!.takeIf { it.segments.isNotEmpty() }?.run {
                loadElapsedTimes(
                    workTime = workTime,
                    breakTime = breakTime,
                    lastSegment = segments.last()
                )
            } ?: run {
                _elapsedWorkSeconds.update { 0 }
                _elapsedBreakMinutes.update { 0 }
            }

            _internalState.update { State.READY }

            startCollection()
        }.invokeOnCompletion {
            cause ->

            if (cause == null) {
                onReady()
            } else {
                throw cause
            }
        }
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
                    breakTime.seconds.plus(sinceStarted).toInt()
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
            if (taskRepository.hasActiveTask()) {
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
                    onReady = ::reactivateAfterExit
                )
            } else {
                _internalState.update { State.DORMANT }
            }
        }
    }


    private fun startCollection() {
        collectionJob = coroutineScope.launch {
            combine (
                _internalState,
                _loadedTask,
                _elapsedWorkSeconds,
                _elapsedBreakMinutes
            ) {
                taskState, loadedTask, workTime, breakTime ->

                Log.i("TimerStateFlow", "$loadedTask")

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
            }.collect {
                state ->
                _timerState.update { state }
            }
        }
    }


    private fun cancelCollection() {
        collectionJob?.cancel()
        collectionJob = null
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
    private suspend fun Task.updateTaskStatus(newStatus: ExecutionStatus) {
        Log.i("TaskStatus", "Updated task's status to $newStatus")
        taskRepository.updateTask(copy(status = newStatus))
    }

    private suspend fun Task.endLastSegment() {
        taskRepository.updateSegment(segments.last().run {
            copy(duration = Duration.between(startTime, Instant.now()))
        })
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

    private fun enqueueDBWrite(writeOp: suspend CoroutineScope.() -> Unit) {
        Log.i("DBWrite", "${dbWriteJob?.isActive?:false}")
        if (dbWriteJob?.isActive?:true) {
            dbWriteJob = coroutineScope.launch {
                writeOp()

                while (dbWritesBuffer.isNotEmpty()) {
                    dbWritesBuffer.removeFirstOrNull()!!()
                }
            }
            dbWriteJob?.invokeOnCompletion{ throwable ->
                dbWriteJob = null
            }
        } else {
            dbWritesBuffer.add(writeOp)
        }
    }



    /*
        TIMER EXECUTION LOGIC
     */
    private fun reactivateAfterExit() {
        when (_loadedTask.value!!.status) {
            ExecutionStatus.NOT_STARTED,
            ExecutionStatus.SUSPENDED,
            ExecutionStatus.COMPLETED -> throw RuntimeException(
                "Attempted to restore an unrestorable task on load"
            )
            ExecutionStatus.RUNNING -> { setRunning() }
            ExecutionStatus.PAUSED -> { setPaused() }
        }
    }


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


    fun start(taskFlow: StateFlow<Task?>) {
        when (_internalState.value) {
            State.DORMANT -> loadTask(
                taskFlow = taskFlow,
                onReady = ::resume
            )
            State.INIT,
            State.PREPARING,
            State.CLOSING,
            State.READY -> throw IllegalStateException(
                "timer.start() must not be called in ${_timerState.value}"
            )
            State.PAUSED,
            State.RUNNING -> suspend(
                taskFlow
            )
        }
    }


    fun resume() {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.RUNNING,
            State.CLOSING -> throw IllegalStateException(
                "timer.resume() must not be called in ${_timerState.value}"
            )
            State.PAUSED,
            State.READY -> {}
        }

        setRunning()

        enqueueDBWrite {
            _loadedTask.value!!.run {
                Log.i("DBWrite", "Resumed")
                updateTaskStatus(ExecutionStatus.RUNNING)

                // Check if this is a new task before updating the last segment
                if (segments.isNotEmpty()) {
                    endLastSegment()
                }

                startNewSegment(SegmentType.WORK)
            }
        }
    }


    fun pause() {
        when (_internalState.value) {
            State.INIT,
            State.DORMANT,
            State.PREPARING,
            State.PAUSED,
            State.CLOSING,
            State.READY -> throw IllegalStateException(
                "timer.pause() must not be called in ${_timerState.value}"
            )
            State.RUNNING -> {}
        }

        setPaused()

        enqueueDBWrite {
            _loadedTask.value!!.run {
                Log.i("DBWrite", "Paused")
                updateTaskStatus(ExecutionStatus.PAUSED)
                endLastSegment()
                startNewSegment(SegmentType.BREAK)
            }
        }
    }


    fun suspend(replaceWith: StateFlow<Task?>? = null) {
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

        enqueueDBWrite {
            _loadedTask.value!!.run {
                Log.i("DBWrite", "Suspended")
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