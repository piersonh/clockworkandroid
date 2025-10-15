package com.wordco.clockworkandroid.core.domain.repository

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.Marker
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    /**
     * Inserts only the properties associated with the task, not the Segments or Markers.
     * */
    suspend fun insertNewTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(id: Long)

    fun getTask(taskId: Long) : Flow<Task>

    fun getTasks() : Flow<List<Task>>

    fun getTodoTasks() : Flow<List<Task.Todo>>

    fun getCompletedTasks() : Flow<List<CompletedTask>>

    fun getSessionsForProfile(profileId: Long) : Flow<List<Task>>

    suspend fun getActiveTaskId() : Long?

    suspend fun insertSegment(segment: Segment)

    suspend fun updateSegment(segment: Segment)

    suspend fun updateSegmentAndInsertNew(existing: Segment, new: Segment)

    suspend fun insertMarker(marker: Marker)
}