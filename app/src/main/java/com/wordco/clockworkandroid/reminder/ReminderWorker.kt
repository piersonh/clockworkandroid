package com.wordco.clockworkandroid.reminder

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.core.domain.repository.ReminderNotificationManager
import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val notificationManager: ReminderNotificationManager =
        (context.applicationContext as MainApplication).appContainer.reminderNotificationManager
    private val reminderRepository: ReminderRepository =
        (context.applicationContext as MainApplication).appContainer.reminderRepository

    companion object {
        const val KEY_REMINDER_MESSAGE = "reminder_message"
        const val KEY_NOTIFICATION_ID = "notification_id"
        const val KEY_REMINDER_ID = "reminder_id"
        const val KEY_SCHEDULED_TIME = "scheduled_time"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val reminderId = inputData.getLong(KEY_REMINDER_ID, 0)
        if (reminderId == 0L) return@withContext Result.failure()

        val message = inputData.getString(KEY_REMINDER_MESSAGE) ?: "You have a reminder."
        val notificationId = inputData.getInt(KEY_NOTIFICATION_ID, 0)
        val scheduledTime = inputData.getLong(KEY_SCHEDULED_TIME, 0)

        val lateness = Duration.between(Instant.ofEpochMilli(scheduledTime), Instant.now())
        val tolerance = Duration.ofMinutes(5)

        try {
            if (lateness < tolerance) {
                notificationManager.sendReminderNotification(message, notificationId)
                reminderRepository.updateReminderStatus(reminderId, Reminder.Status.COMPLETED)
            } else {
                reminderRepository.updateReminderStatus(reminderId, Reminder.Status.EXPIRED)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("ReminderWorker", "Error updating reminder status", e)
            Result.failure()
        }
    }
}