package com.wordco.clockworkandroid.core.domain.repository

import com.wordco.clockworkandroid.core.domain.model.Reminder
import java.time.Instant

interface ReminderRepository {
    suspend fun insertReminder(reminder: Reminder)
    suspend fun deleteReminder(id: Long)
    suspend fun updateReminderStatus(reminderId: Long, status: String)
    suspend fun updateReminderScheduledTime(reminderId: Long, time: Instant)
}