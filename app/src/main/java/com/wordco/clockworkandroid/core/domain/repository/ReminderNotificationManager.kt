package com.wordco.clockworkandroid.core.domain.repository

interface ReminderNotificationManager {
    fun sendReminderNotification(message: String, notificationId: Int)
}