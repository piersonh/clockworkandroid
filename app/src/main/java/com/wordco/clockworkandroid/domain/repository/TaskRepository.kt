package com.wordco.clockworkandroid.domain.repository

import com.wordco.clockworkandroid.domain.model.Task

interface TaskRepository {
    suspend fun insertTask(task: Task)

    suspend fun getTasks() : List<Task>
}