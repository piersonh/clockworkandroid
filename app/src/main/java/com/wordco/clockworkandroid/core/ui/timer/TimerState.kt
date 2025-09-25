package com.wordco.clockworkandroid.core.ui.timer

import com.wordco.clockworkandroid.core.domain.model.StartedTask

sealed interface TimerState {
    sealed interface Empty : TimerState

    data object Dormant : Empty

    data class Preparing(
        val taskId: Long
    ) : Empty

    data object Closing : Empty

    sealed interface HasTask : TimerState {
        val task: StartedTask
        val elapsedWorkSeconds: Second
        val elapsedBreakMinutes: Int
    }

    sealed interface Active : HasTask

    data class Running(
        override val task: StartedTask,
        override val elapsedWorkSeconds: Second,
        override val elapsedBreakMinutes: Int
    ) : Active

    data class Paused(
        override val task: StartedTask,
        override val elapsedWorkSeconds: Second,
        override val elapsedBreakMinutes: Int
    ) : Active
}