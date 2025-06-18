package com.wordco.clockworkandroid.domain.repository

import com.wordco.clockworkandroid.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun insertTask(task: Task)

    suspend fun getTasks() : Flow<List<Task>>
}