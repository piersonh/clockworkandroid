package com.wordco.clockworkandroid.core.domain.model

import java.time.Instant

data class Reminder(
    val reminderId: Long,
    val sessionId: Long,
    val workRequestId: String,
    val scheduledTime: Instant,
    val status: Status
) {
    enum class Status {
        PENDING, COMPLETED, EXPIRED
    }
}