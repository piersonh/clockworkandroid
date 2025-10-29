package com.wordco.clockworkandroid

import com.wordco.clockworkandroid.core.domain.repository.ReminderNotificationManager

/**
 * A fake implementation of ReminderNotificationManager for testing.
 */
class FakeReminderNotificationManager : ReminderNotificationManager {

    var wasNotificationSent = false
    var lastMessage: String? = null
    var lastNotificationId: Int? = null

    override fun sendReminderNotification(message: String, notificationId: Int) {
        wasNotificationSent = true
        lastMessage = message
        lastNotificationId = notificationId
    }

    // Helper to reset state between tests
    fun clear() {
        wasNotificationSent = false
        lastMessage = null
        lastNotificationId = null
    }
}