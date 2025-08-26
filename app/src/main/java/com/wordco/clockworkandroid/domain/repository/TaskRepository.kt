package com.wordco.clockworkandroid.domain.repository

import com.wordco.clockworkandroid.domain.model.Segment
import com.wordco.clockworkandroid.domain.model.StartedTask
import com.wordco.clockworkandroid.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    /**
     * Inserts all components of a task object.
     * */
    suspend fun insertTask(task: Task)

    /**
     * Inserts only the properties associated with the task, not the Segments or Markers.
     * */
    suspend fun insertNewTask(task: Task)

    suspend fun updateTask(task: Task)

    fun getTask(taskId: Long) : Flow<Task>

    fun getTasks() : Flow<List<Task>>

    suspend fun hasActiveTask() : Boolean

    /**
     * ALWAYS CALL [hasActiveTask] FIRST
     */
    fun getActiveTask() : Flow<StartedTask>

    suspend fun insertSegment(segment: Segment)

    suspend fun updateSegment(segment: Segment)


    suspend fun updateSegmentAndInsertNew(existing: Segment, new: Segment)
}