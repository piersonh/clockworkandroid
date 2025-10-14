package com.wordco.clockworkandroid.timer_feature.domain.model

import com.wordco.clockworkandroid.core.domain.model.Second
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SessionTimer(
    private val coroutineScope: CoroutineScope,
    private val session: StateFlow<StartedTask>,
) {
    sealed interface State {
        val elapsedWorkSeconds: Second
        val elapsedBreakSeconds: Second

        data class Work(
            override val elapsedWorkSeconds: Second,
            override val elapsedBreakSeconds: Second
        ) : State

        data class Break(
            override val elapsedWorkSeconds: Second,
            override val elapsedBreakSeconds: Second
        ) : State
    }

    private val _state: MutableStateFlow<State>
    val state: StateFlow<State>

    private val timer: SegmentTimer
    private var timerCollectJob: Job


    init {
        val lastSegment = session.value.segments.maxBy { it.startTime }
        val missedTime = lastSegment.startTime.toEpochMilli()

        timer = SegmentTimer(
            coroutineScope = coroutineScope,
            startTime = missedTime
        )

         when (lastSegment.type) {
            Segment.Type.WORK -> {
                _state = MutableStateFlow(State.Work(
                    elapsedWorkSeconds = session.value.workTime
                        .plusMillis(missedTime).seconds.toInt(),
                    elapsedBreakSeconds = session.value.breakTime.seconds.toInt()
                ))
            }
            Segment.Type.BREAK -> {
                _state = MutableStateFlow(State.Break(
                    elapsedWorkSeconds = session.value.workTime.seconds.toInt(),
                    elapsedBreakSeconds = session.value.breakTime
                        .plusMillis(missedTime).seconds.toInt()
                ))
            }
            Segment.Type.SUSPEND -> error("cannot load suspended sessions")
        }

        timerCollectJob = coroutineScope.launch {
            timer.elapsedSeconds.collect { seconds ->
                _state.update { currentState ->
                    when(currentState) {
                        is State.Break -> currentState.copy(
                            elapsedBreakSeconds = session.value.breakTime.seconds
                                .plus(seconds).toInt()
                        )
                        is State.Work -> currentState.copy(
                            elapsedWorkSeconds = session.value.workTime.seconds
                                .plus(seconds).toInt()
                        )
                    }
                }
            }
        }

        state = _state.asStateFlow()
    }


    fun setWork() {
        _state.update { currentState ->
            timer.reset(System.currentTimeMillis())
            State.Work(
                elapsedWorkSeconds = currentState.elapsedWorkSeconds,
                elapsedBreakSeconds = currentState.elapsedBreakSeconds
            )
        }
    }

    fun setBreak() {
        _state.update { currentState ->
            timer.reset(System.currentTimeMillis())
            State.Break(
                elapsedWorkSeconds = currentState.elapsedWorkSeconds,
                elapsedBreakSeconds = currentState.elapsedBreakSeconds
            )
        }
    }

    fun stop() {
        timerCollectJob.cancel()
        timer.stop()
    }
}