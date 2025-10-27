package com.wordco.clockworkandroid.reminder

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.repository.SessionReminderScheduler
import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit

class SessionReminderSchedulerImpl (
    private val context: Context
) : SessionReminderScheduler {

    override fun schedule(task: Task): String {
        val scheduledTime = task.dueDate ?: return ""
        val delay = Duration.between(Instant.now(), scheduledTime).toMillis()

        // 2. Create Data
        val data = workDataOf(
            ReminderWorker.KEY_NOTIFICATION_ID to (task.taskId.toInt()),
            ReminderWorker.KEY_REMINDER_MESSAGE to task.name,
            ReminderWorker.KEY_SCHEDULED_TIME to scheduledTime.toEpochMilli()
        )

        // 3. Build Work Request
        val reminderWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        // 4. Enqueue and get ID
        val workId = reminderWorkRequest.id
        WorkManager.getInstance(context).enqueue(reminderWorkRequest)

        // 5. Return the ID
        return workId.toString()
    }

    override fun cancel(workRequestId: String) {
        if (workRequestId.isEmpty()) return
        val workIdAsUUID = UUID.fromString(workRequestId)
        WorkManager.getInstance(context).cancelWorkById(workIdAsUUID)
    }
}