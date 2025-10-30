package com.wordco.clockworkandroid.edit_session_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.core.domain.model.ReminderSchedulingData
import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import com.wordco.clockworkandroid.core.domain.repository.SessionReminderScheduler
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.util.UUID

class CreateSessionUseCase (
    private val sessionRepository: TaskRepository,
    private val getAppEstimateUseCase: GetAppEstimateUseCase,
    private val reminderRepository: ReminderRepository,
    private val scheduler: SessionReminderScheduler,
) {
    suspend operator fun invoke(
        task: NewTask,
        reminderTimes: List<Instant>
    ) {
        val sessionId = if (task.userEstimate != null) {
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

        for (reminderTime in reminderTimes) {
            val workRequestId = UUID.randomUUID()

            val initialReminder = Reminder(
                reminderId = 0,
                sessionId = sessionId,
                workRequestId = workRequestId.toString(),
                scheduledTime = reminderTime,
                status = Reminder.Status.PENDING
            )

            val reminderId = reminderRepository.insertReminder(initialReminder)

            val tempReminderData = ReminderSchedulingData(
                reminderId = reminderId,
                sessionId = sessionId,
                message = task.name,
                scheduledTime = reminderTime,
                notificationId = reminderId.hashCode(),
                workRequestId = workRequestId
            )

            scheduler.schedule(tempReminderData)
        }
    }
}
