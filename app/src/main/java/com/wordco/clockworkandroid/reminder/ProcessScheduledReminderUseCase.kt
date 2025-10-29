package com.wordco.clockworkandroid.reminder

import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.core.domain.repository.ReminderNotificationManager
import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import java.time.Duration
import java.time.Instant

class ProcessScheduledReminderUseCase(
    private val reminderRepository: ReminderRepository,
    private val reminderNotifier: ReminderNotificationManager,
) {
    private val tolerance = Duration.ofMinutes(5)

    suspend operator fun invoke(
        reminderId: Long,
        message: String,
        notificationId: Int,
        scheduledTime: Long
    ) {
        val lateness = Duration.between(Instant.ofEpochMilli(scheduledTime), Instant.now())

        if (lateness < tolerance) {
            reminderNotifier.sendReminderNotification(message, notificationId)
            reminderRepository.updateReminderStatus(reminderId, Reminder.Status.COMPLETED)
        } else {
            reminderRepository.updateReminderStatus(reminderId, Reminder.Status.EXPIRED)
        }
    }
}