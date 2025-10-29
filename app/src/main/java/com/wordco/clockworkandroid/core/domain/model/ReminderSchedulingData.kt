package com.wordco.clockworkandroid.core.domain.model

import java.time.Instant

data class ReminderSchedulingData(
    val reminderId: Long,
    val sessionId: Long,
    val message: String,
    val scheduledTime: Instant,
    val notificationId: Int,
)