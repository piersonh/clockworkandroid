package com.wordco.clockworkandroid.database.data.repository

import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import com.wordco.clockworkandroid.database.data.local.ReminderDao
import com.wordco.clockworkandroid.database.data.local.entities.ReminderEntity
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toReminderEntity
import java.time.Instant

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao
) : ReminderRepository {
    override suspend fun insertReminder(reminder: Reminder) {
        reminderDao.insertReminder(reminder.toReminderEntity())
    }

    override suspend fun deleteReminder(id: Long) {
        reminderDao.deleteReminder(id)
    }

    override suspend fun updateReminderStatus(reminderId: Long, status: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateReminderScheduledTime(reminderId: Long, time: Instant) {
        TODO("Not yet implemented")
    }
}