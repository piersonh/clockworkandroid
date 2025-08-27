package com.wordco.clockworkandroid.core.domain.timer

import com.wordco.clockworkandroid.core.domain.model.StartedTask

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