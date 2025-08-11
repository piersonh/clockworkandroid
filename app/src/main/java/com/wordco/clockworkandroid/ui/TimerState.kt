package com.wordco.clockworkandroid.ui

import com.wordco.clockworkandroid.domain.model.ExecutionStatus


// add empty state?
// add finished state?

enum class TimerState {
    WAITING,
    RUNNING,
    BREAK,
    SUSPENDED
}


fun ExecutionStatus.toTimerState() : TimerState{
    return when (this) {
        ExecutionStatus.NOT_STARTED -> TimerState.WAITING
        ExecutionStatus.RUNNING -> TimerState.RUNNING
        ExecutionStatus.PAUSED -> TimerState.BREAK
        ExecutionStatus.SUSPENDED -> TimerState.SUSPENDED
        ExecutionStatus.COMPLETED -> TODO()
    }
}