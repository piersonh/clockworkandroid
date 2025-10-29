package com.wordco.clockworkandroid.core.domain.repository

import com.wordco.clockworkandroid.core.domain.model.ReminderSchedulingData

interface SessionReminderScheduler {
    /**
     * Schedules a reminder.
     * @return The unique String ID for the scheduled work.
     */
    fun schedule(reminderData: ReminderSchedulingData): String

    /**
     * Cancels a previously scheduled reminder.
     */
    fun cancel(workRequestId: String)

    /**
     * Cancels all previously scheduled reminders for a single session.
     */
    fun cancelAllForSession(sessionId: Long)
}