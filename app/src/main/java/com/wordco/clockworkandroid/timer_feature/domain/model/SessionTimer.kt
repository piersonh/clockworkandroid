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
    enum class State {
        WORK,BREAK
    }

    private val _state: MutableStateFlow<State>
    val state: StateFlow<State>

    private val _elapsedWorkSeconds: MutableStateFlow<Second>
    val elapsedWorkSeconds: StateFlow<Second>
    private val _elapsedBreakSeconds: MutableStateFlow<Second>
    val elapsedBreakSeconds: StateFlow<Second>

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
                _elapsedWorkSeconds = MutableStateFlow(
                    session.value.workTime.plusMillis(missedTime).seconds.toInt()
                )
                _elapsedBreakSeconds = MutableStateFlow(session.value.breakTime.seconds.toInt())

                _state = MutableStateFlow(State.WORK)
            }
            Segment.Type.BREAK -> {
                _elapsedWorkSeconds = MutableStateFlow(session.value.workTime.seconds.toInt())
                _elapsedBreakSeconds = MutableStateFlow(
                    session.value.breakTime.plusMillis(missedTime).seconds.toInt()
                )

                _state = MutableStateFlow(State.BREAK)
            }
            Segment.Type.SUSPEND -> error("cannot load suspended sessions")
        }

        timerCollectJob = coroutineScope.launch {
            timer.elapsedSeconds.collect { seconds ->
                when (_state.value) {
                    State.WORK -> _elapsedWorkSeconds.update {
                        session.value.workTime.seconds.plus(seconds).toInt()
                    }
                    State.BREAK -> _elapsedBreakSeconds.update {
                        session.value.breakTime.seconds.plus(seconds).toInt()
                    }
                }
            }
        }

        state = _state.asStateFlow()
        elapsedWorkSeconds = _elapsedWorkSeconds.asStateFlow()
        elapsedBreakSeconds = _elapsedBreakSeconds.asStateFlow()
    }


    fun setWork() {
        if (_state.value == State.WORK) return

        timer.reset(System.currentTimeMillis())

        _state.update { State.WORK }
    }

    fun setBreak() {
        if (_state.value == State.BREAK) return

        timer.reset(System.currentTimeMillis())

        _state.update { State.BREAK }
    }

    fun stop() {
        timerCollectJob.cancel()
        timer.stop()
    }
}