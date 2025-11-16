package com.wordco.clockworkandroid.user_stats_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetAllSessionsUseCase(
    private val sessionRepository: TaskRepository,
) {
    operator fun invoke(): Flow<List<Task>> {
        return sessionRepository.getTasks()
    }
}