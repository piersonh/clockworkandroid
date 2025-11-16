package com.wordco.clockworkandroid.database.data.repository

import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import com.wordco.clockworkandroid.database.data.local.ReminderDao
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toReminder
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toReminderEntity
import com.wordco.clockworkandroid.database.data.util.fromReminderStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao
) : ReminderRepository {
    override suspend fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder.toReminderEntity())
    }

    override suspend fun deleteReminder(id: Long) {
        reminderDao.deleteReminder(id)
    }

    override suspend fun deleteAllRemindersForSession(sessionId: Long) {
        reminderDao.deleteAllRemindersForSession(sessionId)
    }

    override suspend fun deleteAllPendingRemindersForSession(sessionId: Long) {
        reminderDao.deleteAllPendingRemindersForSession(sessionId)
    }

    override suspend fun updateReminderStatus(
        reminderId: Long,
        status: Reminder.Status
    ) {
        reminderDao.updateStatus(reminderId, fromReminderStatus(status))
    }

    override fun getRemindersForSession(sessionId: Long): Flow<List<Reminder>> {
        return reminderDao.getRemindersForSession(sessionId).map { list ->
            list.map { it.toReminder() }
        }
    }
}