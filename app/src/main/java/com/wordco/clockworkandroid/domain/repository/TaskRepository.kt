package com.wordco.clockworkandroid.domain.repository

import com.wordco.clockworkandroid.domain.model.Segment
import com.wordco.clockworkandroid.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun insertTask(task: Task)

    fun getTask(taskId: Long) : Flow<Task>

    fun getTasks() : Flow<List<Task>>

    suspend fun insertSegment(segment: Segment)
}