package com.wordco.clockworkandroid.core.domain.repository

import com.wordco.clockworkandroid.core.domain.model.Task

interface SessionReminderScheduler {
    /**
     * Schedules a reminder for a task.
     * @return The unique String ID for the scheduled work.
     */
    fun schedule(task: Task): String

    /**
     * Cancels a previously scheduled reminder.
     */
    fun cancel(workRequestId: String)
}