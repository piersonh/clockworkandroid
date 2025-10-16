package com.wordco.clockworkandroid.timer_feature.domain.model

import com.wordco.clockworkandroid.core.domain.model.Second
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.timer_feature.domain.model.SessionTimer.State.Break
import com.wordco.clockworkandroid.timer_feature.domain.model.SessionTimer.State.Work
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import java.time.Duration
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class SessionTimer(
    coroutineScope: CoroutineScope,
    initialWorkSecondsExcludingCurrentSegment: Second,
    initialBreakSecondsExcludingCurrentSegment: Second,
    currentSegmentStartTime: Instant,
    currentSegmentType: Segment.Type,
) {
    sealed interface State {
        val elapsedWorkSeconds: Second
        val elapsedBreakSeconds: Second
        val currentSegmentElapsedSeconds: Second

        data class Work(
            override val elapsedWorkSeconds: Second,
            override val elapsedBreakSeconds: Second,
            override val currentSegmentElapsedSeconds: Second = 0
        ) : State

        data class Break(
            override val elapsedWorkSeconds: Second,
            override val elapsedBreakSeconds: Second,
            override val currentSegmentElapsedSeconds: Second = 0
        ) : State
    }

    val state: StateFlow<State>

    private sealed interface InternalState {
        val totalWorkSecondsExcludingCurrentSegment: Second
        val totalBreakSecondsExcludingCurrentSegment: Second
        val currentSegmentStartTime: Instant

        data class Work(
            override val totalWorkSecondsExcludingCurrentSegment: Second,
            override val totalBreakSecondsExcludingCurrentSegment: Second,
            override val currentSegmentStartTime: Instant
        ) : InternalState

        data class Break(
            override val totalWorkSecondsExcludingCurrentSegment: Second,
            override val totalBreakSecondsExcludingCurrentSegment: Second,
            override val currentSegmentStartTime: Instant
        ) : InternalState
    }

    private val internalState: MutableStateFlow<InternalState>

    init {
        val initialState: InternalState = when (currentSegmentType) {
            Segment.Type.WORK -> InternalState.Work(
                totalWorkSecondsExcludingCurrentSegment = initialWorkSecondsExcludingCurrentSegment,
                totalBreakSecondsExcludingCurrentSegment = initialBreakSecondsExcludingCurrentSegment,
                currentSegmentStartTime = currentSegmentStartTime,
            )
            Segment.Type.BREAK -> InternalState.Break(
                totalWorkSecondsExcludingCurrentSegment = initialWorkSecondsExcludingCurrentSegment,
                totalBreakSecondsExcludingCurrentSegment = initialBreakSecondsExcludingCurrentSegment,
                currentSegmentStartTime = currentSegmentStartTime,
            )
            Segment.Type.SUSPEND -> error("cannot load suspended sessions")
        }
        internalState = MutableStateFlow(initialState)

        val initialCurrentSegmentElapsedSeconds = Duration.between(currentSegmentStartTime, Instant.now()).seconds

        state = internalState.transformLatest { currentState ->
            while (true) {
                val currentSegmentElapsed = Duration.between(currentState.currentSegmentStartTime, Instant.now()).seconds
                val publicState = currentState.toPublicState(currentSegmentElapsed.toInt())
                emit(publicState)
                delay(100)
            }
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = initialState.toPublicState(
                initialCurrentSegmentElapsedSeconds.toInt()
            )
        )
    }

    private fun InternalState.toPublicState(currentSegmentElapsedSeconds: Second) : State {
        return when (this) {
            is InternalState.Work -> Work(
                elapsedWorkSeconds = totalWorkSecondsExcludingCurrentSegment + currentSegmentElapsedSeconds,
                elapsedBreakSeconds = totalBreakSecondsExcludingCurrentSegment,
                currentSegmentElapsedSeconds = currentSegmentElapsedSeconds
            )
            is InternalState.Break -> Break(
                elapsedWorkSeconds = totalWorkSecondsExcludingCurrentSegment,
                elapsedBreakSeconds = totalBreakSecondsExcludingCurrentSegment + currentSegmentElapsedSeconds,
                currentSegmentElapsedSeconds = currentSegmentElapsedSeconds
            )
        }
    }


    fun setWork(now: Instant) {
        internalState.update { currentState ->
            val lastSegmentDuration = Duration.between(currentState.currentSegmentStartTime, now).seconds.toInt()
            when (currentState) {
                is InternalState.Work -> {
                    InternalState.Work(
                        totalWorkSecondsExcludingCurrentSegment = currentState.totalWorkSecondsExcludingCurrentSegment + lastSegmentDuration,
                        totalBreakSecondsExcludingCurrentSegment = currentState.totalBreakSecondsExcludingCurrentSegment,
                        currentSegmentStartTime = now
                    )
                }
                is InternalState.Break -> {
                    InternalState.Work(
                        totalWorkSecondsExcludingCurrentSegment = currentState.totalWorkSecondsExcludingCurrentSegment,
                        totalBreakSecondsExcludingCurrentSegment = currentState.totalBreakSecondsExcludingCurrentSegment + lastSegmentDuration,
                        currentSegmentStartTime = now
                    )
                }
            }
        }
    }

    fun setBreak(now: Instant) {
        internalState.update { currentState ->
            val lastSegmentDuration = Duration.between(currentState.currentSegmentStartTime, now).seconds.toInt()
            when (currentState) {
                is InternalState.Work -> {
                    InternalState.Break(
                        totalWorkSecondsExcludingCurrentSegment = currentState.totalWorkSecondsExcludingCurrentSegment + lastSegmentDuration,
                        totalBreakSecondsExcludingCurrentSegment = currentState.totalBreakSecondsExcludingCurrentSegment,
                        currentSegmentStartTime = now
                    )
                }
                is InternalState.Break -> {
                    InternalState.Break(
                        totalWorkSecondsExcludingCurrentSegment = currentState.totalWorkSecondsExcludingCurrentSegment,
                        totalBreakSecondsExcludingCurrentSegment = currentState.totalBreakSecondsExcludingCurrentSegment + lastSegmentDuration,
                        currentSegmentStartTime = now
                    )
                }
            }
        }
    }
}