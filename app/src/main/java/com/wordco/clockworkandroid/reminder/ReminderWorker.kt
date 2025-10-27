package com.wordco.clockworkandroid.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.repository.ReminderNotificationManager
import java.time.Duration
import java.time.Instant

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val notificationManager: ReminderNotificationManager =
        (context.applicationContext as MainApplication).appContainer.reminderNotificationManager

    // A key to retrieve the message from the input data
    companion object {
        const val KEY_REMINDER_MESSAGE = "reminder_message"
        const val KEY_NOTIFICATION_ID = "notification_id"

        const val KEY_SCHEDULED_TIME = "scheduled_time"
    }

    override suspend fun doWork(): Result {
        val message = inputData.getString(KEY_REMINDER_MESSAGE) ?: "You have a reminder."
        val notificationId = inputData.getInt(KEY_NOTIFICATION_ID, 0)
        val scheduledTime = inputData.getLong(KEY_SCHEDULED_TIME, 0)

        val lateness = Duration.between(Instant.ofEpochMilli(scheduledTime), Instant.now())
        val tolerance = Duration.ofMinutes(5)

        if (lateness < tolerance) {
            notificationManager.sendReminderNotification(message,notificationId)
        }

        return Result.success()
    }
}