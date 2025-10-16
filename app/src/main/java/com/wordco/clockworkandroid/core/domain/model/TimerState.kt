package com.wordco.clockworkandroid.core.domain.model


sealed interface TimerState {
    sealed interface Empty : TimerState

    data object Dormant : Empty

    data class Preparing(
        val taskId: Long
    ) : Empty

    data object Closing : Empty

    sealed interface Active : TimerState {
        val taskId: Long
        val totalElapsedSeconds: Second
        val currentSegmentElapsedSeconds: Second
    }

    data class Running(
        override val taskId: Long,
        override val totalElapsedSeconds: Second,
        override val currentSegmentElapsedSeconds: Second,
    ) : Active

    data class Paused(
        override val taskId: Long,
        override val totalElapsedSeconds: Second,
        override val currentSegmentElapsedSeconds: Second,
    ) : Active
}