package com.wordco.clockworkandroid.edit_session_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import com.wordco.clockworkandroid.core.domain.repository.SessionReminderScheduler
import com.wordco.clockworkandroid.core.domain.model.ReminderSchedulingData
import java.time.Instant

class CreateReminderUseCase(
    private val reminderRepository: ReminderRepository,
    private val scheduler: SessionReminderScheduler,
) {
    suspend operator fun invoke(sessionId: Long, message: String, time: Instant) {
        // 1. Create initial entity (without workId)
        val initialReminder = Reminder(
            reminderId = 0,
            sessionId = sessionId,
            workRequestId = "",
            scheduledTime = time,
            status = Reminder.Status.PENDING
        )

        // 2. Insert into DB to get the generated reminderId
        val reminderId = reminderRepository.insertReminder(initialReminder)

        // 3. Prepare data for scheduler
        val tempReminderData = ReminderSchedulingData(
            reminderId = reminderId,
            sessionId = sessionId,
            message = message,
            scheduledTime = time,
            notificationId = reminderId.hashCode()
        )

        // 4. Schedule the work
        val workId = scheduler.schedule(tempReminderData)

        // 5. Update the DB record with the workId
        val finalReminder = initialReminder.copy(reminderId = reminderId, workRequestId = workId)
        reminderRepository.updateReminder(finalReminder)
    }
}