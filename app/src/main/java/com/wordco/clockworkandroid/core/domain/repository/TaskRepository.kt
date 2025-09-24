package com.wordco.clockworkandroid.core.domain.repository

import com.wordco.clockworkandroid.core.domain.model.Marker
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task
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

    fun getSessionsForProfile(profileId: Long) : Flow<List<Task>>

    suspend fun hasActiveTask() : Boolean

    suspend fun getActiveTask() : Flow<StartedTask>?

    suspend fun insertSegment(segment: Segment)

    suspend fun updateSegment(segment: Segment)

    suspend fun updateSegmentAndInsertNew(existing: Segment, new: Segment)

    suspend fun insertMarker(marker: Marker)
}