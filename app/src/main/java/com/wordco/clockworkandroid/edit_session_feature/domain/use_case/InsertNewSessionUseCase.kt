package com.wordco.clockworkandroid.edit_session_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.core.domain.model.ReminderSchedulingData
import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import com.wordco.clockworkandroid.core.domain.repository.SessionReminderScheduler
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import java.time.Instant

class InsertNewSessionUseCase (
    private val sessionRepository: TaskRepository,
    private val getAppEstimateUseCase: GetAppEstimateUseCase,
    private val reminderRepository: ReminderRepository,
    private val scheduler: SessionReminderScheduler,
) {
    suspend operator fun invoke(task: NewTask) {
        if (task.userEstimate != null) {
            val sessionHistory = sessionRepository.getCompletedTasks().first()
                .filter { it.userEstimate != null }

            val appEstimate = getAppEstimateUseCase(
                todoSession = task,
                sessionHistory = sessionHistory
            )
            sessionRepository.insertNewTask(task.copy(appEstimate = appEstimate))
        } else {
            sessionRepository.insertNewTask(task)
        }

        if (task.dueDate != null && task.dueDate > Instant.now()) {
            val initialReminder = Reminder(
                reminderId = 0,
                sessionId = task.taskId,
                workRequestId = "",
                scheduledTime = task.dueDate,
                status = Reminder.Status.PENDING
            )

            // 2. Insert into DB to get the generated reminderId
            val reminderId = reminderRepository.insertReminder(initialReminder)

            // 3. Prepare data for scheduler
            val tempReminderData = ReminderSchedulingData(
                reminderId = reminderId,
                sessionId = task.taskId,
                message = task.name,
                scheduledTime = task.dueDate,
                notificationId = reminderId.hashCode()
            )

            // 4. Schedule the work
            val workId = scheduler.schedule(tempReminderData)

            // 5. Update the DB record with the workId
            val finalReminder = initialReminder.copy(reminderId = reminderId, workRequestId = workId)
            reminderRepository.updateReminder(finalReminder)
        }
    }
}
