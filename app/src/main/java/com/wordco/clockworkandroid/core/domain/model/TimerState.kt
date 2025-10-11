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
        val elapsedWorkSeconds: Second
        val elapsedBreakMinutes: Int
    }

    data class Running(
        override val taskId: Long,
        override val elapsedWorkSeconds: Second,
        override val elapsedBreakMinutes: Int
    ) : Active

    data class Paused(
        override val taskId: Long,
        override val elapsedWorkSeconds: Second,
        override val elapsedBreakMinutes: Int
    ) : Active
}