package com.wordco.clockworkandroid.edit_session_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import com.wordco.clockworkandroid.core.domain.repository.SessionReminderScheduler
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import java.time.Instant

class UpdateSessionUseCase(
    private val sessionRepository: TaskRepository,
    private val getAppEstimateUseCase: GetAppEstimateUseCase,
    private val reminderRepository: ReminderRepository,
    private val scheduler: SessionReminderScheduler,
) {
    suspend operator fun invoke(
        newSession: Task,
        reminderTimes: List<Instant>
    ) {

        val taskToSave = when (newSession) {
            is NewTask -> {
                val oldSession = sessionRepository.getTask(newSession.taskId).first()
                val shouldRecalculate = newSession.userEstimate != null &&
                        (newSession.profileId != oldSession.profileId ||
                                newSession.difficulty != oldSession.difficulty ||
                                newSession.userEstimate != oldSession.userEstimate)

                if (shouldRecalculate) {
                    val sessionHistory = sessionRepository.getCompletedTasks().first()
                        .filter { it.userEstimate != null }
                    val appEstimate = getAppEstimateUseCase(
                        todoSession = newSession,
                        sessionHistory = sessionHistory
                    )
                    newSession.copy(appEstimate = appEstimate)
                } else {
                    newSession
                }
            }

            is StartedTask, is CompletedTask -> newSession
        }

        sessionRepository.updateTask(taskToSave)

        scheduler.cancelAllForSession(taskToSave.taskId)
        reminderRepository.deleteAllRemindersForSession(taskToSave.taskId)

        for (reminderTime in reminderTimes) {

        }
    }
}