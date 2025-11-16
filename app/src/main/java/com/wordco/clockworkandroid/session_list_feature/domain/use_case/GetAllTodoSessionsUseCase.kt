package com.wordco.clockworkandroid.session_list_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetAllTodoSessionsUseCase(
    private val sessionRepository: TaskRepository,
) {
    operator fun invoke(): Flow<List<Task.Todo>> {
        return sessionRepository.getTodoTasks()
    }
}