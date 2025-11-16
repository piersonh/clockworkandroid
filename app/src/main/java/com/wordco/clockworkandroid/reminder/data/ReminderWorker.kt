package com.wordco.clockworkandroid.reminder.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.reminder.domain.use_case.ProcessScheduledReminderUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val processReminderUseCase: ProcessScheduledReminderUseCase =
        (context.applicationContext as MainApplication).appContainer.processScheduledReminderUseCase

    companion object {
        const val KEY_REMINDER_MESSAGE = "reminder_message"
        const val KEY_NOTIFICATION_ID = "notification_id"
        const val KEY_REMINDER_ID = "reminder_id"
        const val KEY_SCHEDULED_TIME = "scheduled_time"
        const val KEY_SESSION_ID = "session_id"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val reminderId = inputData.getLong(KEY_REMINDER_ID, 0)
        val sessionId = inputData.getLong(KEY_SESSION_ID, 0)
        if (reminderId == 0L || sessionId == 0L) return@withContext Result.failure()

        val message = inputData.getString(KEY_REMINDER_MESSAGE) ?: "You have a reminder."
        val notificationId = inputData.getInt(KEY_NOTIFICATION_ID, 0)
        val scheduledTime = inputData.getLong(KEY_SCHEDULED_TIME, 0)

        try {
            processReminderUseCase(
                reminderId = reminderId,
                sessionId = sessionId,
                message = message,
                notificationId = notificationId,
                scheduledTime = scheduledTime
            )
            Result.success()
        } catch (e: Exception) {
            Log.e("ReminderWorker", "Error updating reminder status", e)
            Result.failure()
        }
    }
}