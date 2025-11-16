package com.wordco.clockworkandroid.core.data.fake

import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicLong

/**
 * A fake implementation of ReminderRepository for testing.
 * It stores reminders in an in-memory map.
 */
class FakeReminderRepository(
    initialValues: List<Reminder> = emptyList()
) : ReminderRepository {

    private val reminders = MutableStateFlow<Map<Long, Reminder>>(emptyMap())
    private val nextId = AtomicLong(1L) // For auto-generating IDs

    init {
        reminders.value = initialValues.associateBy { it.reminderId }
        val maxId = initialValues.maxOfOrNull { it.reminderId } ?: 0L
        nextId.set(maxId + 1)
    }

    override suspend fun insertReminder(reminder: Reminder): Long {
        val newId = if (reminder.reminderId == 0L) {
            nextId.getAndIncrement()
        } else {
            reminder.reminderId
        }

        nextId.getAndUpdate { current -> maxOf(current, newId + 1) }

        val newReminder = reminder.copy(reminderId = newId)
        reminders.update { map ->
            map + (newId to newReminder)
        }
        return newId
    }

    override suspend fun deleteReminder(id: Long) {
        reminders.update { map ->
            map - id
        }
    }

    override suspend fun deleteAllRemindersForSession(sessionId: Long) {
        reminders.update { map ->
            map.filterNot { (_, reminder) ->
                reminder.sessionId == sessionId
            }
        }
    }

    override suspend fun deleteAllPendingRemindersForSession(sessionId: Long) {
        reminders.update { map ->
            map.filterNot { (_, reminder) ->
                reminder.sessionId == sessionId && reminder.status == Reminder.Status.PENDING
            }
        }
    }


    override suspend fun updateReminderStatus(reminderId: Long, status: Reminder.Status) {
        reminders.update { map ->
            val reminder = map[reminderId] ?: error("Reminder not found")

            val updatedReminder = reminder.copy(status = status)

            map + (reminderId to updatedReminder)
        }
    }


    override fun getRemindersForSession(sessionId: Long): Flow<List<Reminder>> {
        return reminders.map { map ->
            map.values.filter { it.sessionId == sessionId }
        }
    }

}