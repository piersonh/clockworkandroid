package com.wordco.clockworkandroid.edit_session_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first

class UpdateSessionUseCase(
    private val sessionRepository: TaskRepository,
    private val getAppEstimateUseCase: GetAppEstimateUseCase
) {
    suspend operator fun invoke(newSession: Task, oldSession: Task) {

        val taskToSave = when (newSession) {
            is NewTask -> {

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
    }
}