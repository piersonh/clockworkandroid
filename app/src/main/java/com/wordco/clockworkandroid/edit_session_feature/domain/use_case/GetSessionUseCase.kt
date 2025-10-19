package com.wordco.clockworkandroid.edit_session_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetSessionUseCase(
    private val sessionRepository: TaskRepository
) {
    operator fun invoke(sessionId: Long): Flow<Task> {
        return sessionRepository.getTask(sessionId)
    }
}