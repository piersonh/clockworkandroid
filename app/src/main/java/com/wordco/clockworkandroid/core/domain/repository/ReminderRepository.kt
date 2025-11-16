package com.wordco.clockworkandroid.core.domain.repository

import com.wordco.clockworkandroid.core.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    suspend fun insertReminder(reminder: Reminder): Long
    suspend fun deleteReminder(id: Long)
    suspend fun deleteAllRemindersForSession(sessionId: Long)
    suspend fun deleteAllPendingRemindersForSession(sessionId: Long)
    suspend fun updateReminderStatus(reminderId: Long, status: Reminder.Status)
    fun getRemindersForSession(sessionId: Long): Flow<List<Reminder>>
}