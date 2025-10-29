package com.wordco.clockworkandroid.edit_session_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import com.wordco.clockworkandroid.core.domain.repository.SessionReminderScheduler
import com.wordco.clockworkandroid.core.domain.model.ReminderSchedulingData
import java.time.Instant

class UpdateReminderUseCase(
    private val reminderRepository: ReminderRepository,
    private val scheduler: SessionReminderScheduler
) {
    suspend operator fun invoke(
        reminder: Reminder,
        newMessage: String,
        newTime: Instant
    ) {
        // 2. Cancel the old work request
        scheduler.cancel(reminder.workRequestId)

        // 3. Schedule the new work request
        val tempReminderData = ReminderSchedulingData(
            reminderId = reminder.reminderId,
            sessionId = reminder.sessionId,
            message = newMessage,
            scheduledTime = newTime,
            notificationId = reminder.reminderId.hashCode()
        )
        val newWorkId = scheduler.schedule(tempReminderData)

        // 4. Update the DB record
        val updatedReminder = reminder.copy(
            workRequestId = newWorkId,
            scheduledTime = newTime,
            status = Reminder.Status.PENDING // Reset status
        )
        reminderRepository.updateReminder(updatedReminder) // Add update function to Repo/DAO
    }
}