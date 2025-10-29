package com.wordco.clockworkandroid.user_stats_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetAllCompletedSessionsUseCase(
    private val sessionRepository: TaskRepository,
) {
    operator fun invoke(): Flow<List<CompletedTask>> {
        return sessionRepository.getCompletedTasks()
    }
}