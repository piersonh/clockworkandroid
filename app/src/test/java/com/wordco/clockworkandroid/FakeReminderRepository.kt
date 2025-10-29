package com.wordco.clockworkandroid

import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.atomic.AtomicLong

/**
 * A fake implementation of ReminderRepository for testing.
 * It stores reminders in an in-memory map.
 */
class FakeReminderRepository : ReminderRepository {

    private val reminders = MutableStateFlow<Map<Long, Reminder>>(emptyMap())
    private val nextId = AtomicLong(1L) // For auto-generating IDs

    override suspend fun insertReminder(reminder: Reminder): Long {
        val newId = if (reminder.reminderId == 0L) {
            nextId.getAndIncrement()
        } else {
            reminder.reminderId
        }

        // Ensure the next auto-generated ID is higher than any manually inserted ID
        nextId.getAndUpdate { current -> maxOf(current, newId + 1) }

        val newReminder = reminder.copy(reminderId = newId)
        reminders.value += (newId to newReminder)
        return newId
    }

    override suspend fun deleteReminder(id: Long) {
        reminders.value -= id
    }

    override suspend fun deleteAllRemindersForSession(sessionId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllPendingRemindersForSession(sessionId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun updateReminder(reminder: Reminder) {
        if (reminders.value.containsKey(reminder.reminderId)) {
            reminders.value += (reminder.reminderId to reminder)
        }
    }

    override suspend fun updateReminderStatus(reminderId: Long, status: Reminder.Status) {
        val reminder = reminders.value[reminderId]
        if (reminder != null) {
            updateReminder(reminder.copy(status = status))
        }
    }

    override fun getReminder(id: Long): Flow<Reminder> {
        TODO("Not yet implemented")
    }

    override fun getRemindersForSession(sessionId: Long): Flow<List<Reminder>> {
        return reminders.map { map ->
            map.values.filter { it.sessionId == sessionId }
        }
    }

}