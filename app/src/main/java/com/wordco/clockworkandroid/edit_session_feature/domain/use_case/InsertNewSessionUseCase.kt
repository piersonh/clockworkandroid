package com.wordco.clockworkandroid.edit_session_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first

class InsertNewSessionUseCase (
    private val sessionRepository: TaskRepository,
    private val getAppEstimateUseCase: GetAppEstimateUseCase
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
    }
}
