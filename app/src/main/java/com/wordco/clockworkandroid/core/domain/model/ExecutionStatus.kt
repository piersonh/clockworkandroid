package com.wordco.clockworkandroid.core.domain.model

// TODO: rethink what the purpose of this task object is
// and if it should have timer execution states or just completion states
enum class ExecutionStatus {
    NOT_STARTED,
    RUNNING,
    PAUSED,
    SUSPENDED,
    COMPLETED
}