package com.wordco.clockworkandroid.reminder

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.wordco.clockworkandroid.core.domain.model.ReminderSchedulingData
import com.wordco.clockworkandroid.core.domain.repository.SessionReminderScheduler
import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit

class SessionReminderSchedulerImpl (
    context: Context
) : SessionReminderScheduler {

    private val workManager = WorkManager.getInstance(context)

    override fun schedule(reminderData: ReminderSchedulingData) {
        val scheduledTime = reminderData.scheduledTime
        val delay = Duration.between(Instant.now(), scheduledTime).toMillis()

        val data = workDataOf(
            ReminderWorker.KEY_REMINDER_ID to reminderData.reminderId,
            ReminderWorker.KEY_NOTIFICATION_ID to reminderData.notificationId,
            ReminderWorker.KEY_REMINDER_MESSAGE to reminderData.message,
            ReminderWorker.KEY_SCHEDULED_TIME to scheduledTime.toEpochMilli(),
            ReminderWorker.KEY_SESSION_ID to reminderData.sessionId,
        )

        val reminderWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setId(reminderData.workRequestId)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("reminder") // General tag for all reminders
            .addTag("session_${reminderData.sessionId}") // Tag specific to the session
            .build()

        workManager.enqueue(reminderWorkRequest)
    }

    override fun cancel(workRequestId: String) {
        if (workRequestId.isEmpty()) return
        val workIdAsUUID = UUID.fromString(workRequestId)
        workManager.cancelWorkById(workIdAsUUID)
    }

    override fun cancelAllForSession(sessionId: Long) {
        val sessionTag = "session_$sessionId"
        workManager.cancelAllWorkByTag(sessionTag)
    }
}