package com.wordco.clockworkandroid.core.domain.repository

import com.wordco.clockworkandroid.core.domain.model.Reminder
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface ReminderRepository {
    suspend fun insertReminder(reminder: Reminder): Long
    suspend fun deleteReminder(id: Long)
    suspend fun deleteAllRemindersForSession(sessionId: Long)
    suspend fun deleteAllPendingRemindersForSession(sessionId: Long)
    suspend fun updateReminder(reminder: Reminder)
    suspend fun updateReminderStatus(reminderId: Long, status: Reminder.Status)
    fun getReminder(id: Long): Flow<Reminder>
    fun getRemindersForSession(sessionId: Long): Flow<List<Reminder>>
}