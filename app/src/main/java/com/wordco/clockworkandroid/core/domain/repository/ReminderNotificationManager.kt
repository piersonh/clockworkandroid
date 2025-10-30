package com.wordco.clockworkandroid.core.domain.repository

interface ReminderNotificationManager {
    fun sendReminderNotification(
        message: String,
        sessionId: Long,
        notificationId: Int
    )
}