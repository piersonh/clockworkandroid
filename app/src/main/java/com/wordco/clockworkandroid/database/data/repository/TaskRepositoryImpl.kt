package com.wordco.clockworkandroid.database.data.repository

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.Marker
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.database.data.local.TaskDao
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toMarkerEntity
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toSegmentEntity
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toTask
import com.wordco.clockworkandroid.database.data.local.entities.mapper.toTaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl (
    private val taskDao: TaskDao,
) : TaskRepository {
    override suspend fun insertTask(task: Task) {
        taskDao.insertTask(task.toTaskEntity())

        when (task) {
            is NewTask -> {}
            is CompletedTask -> {
                taskDao.insertSegments(task.segments.map { segment -> segment.toSegmentEntity() })
                taskDao.insertMarkers(task.markers.map { marker -> marker.toMarkerEntity() })

            }
            is StartedTask -> {
                taskDao.insertSegments(task.segments.map { segment -> segment.toSegmentEntity() })
                taskDao.insertMarkers(task.markers.map { marker -> marker.toMarkerEntity() })
            }
        }

    }

    override suspend fun insertNewTask(task: Task) {
        taskDao.insertTask(task.toTaskEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toTaskEntity())
    }

    override suspend fun deleteTask(id: Long) {
        taskDao.deleteTaskWithExecutionData(id)
    }

    override fun getTask(taskId: Long): Flow<Task> {
        return taskDao.getTaskWithExecutionData(taskId)
            .filterNotNull()
            .map {
                it.toTask()
            }
    }

    override fun getTasks(): Flow<List<Task>> {
        return taskDao.getTasksWithExecutionData()
            .map { taskList ->
                taskList.map { it.toTask() }
            }
    }

    override fun getTodoTasks(): Flow<List<Task.Todo>> {
        return taskDao
            .getTodoTasksWithExecutionData()
            .map { todoSessions ->
                todoSessions.map { it.toTask() as Task.Todo }
            }
    }

    override fun getCompletedTasks(): Flow<List<CompletedTask>> {
        return taskDao
            .getCompletedTasksWithExecutionData()
            .map { todoSessions ->
                todoSessions.map { it.toTask() as CompletedTask }
            }
    }

    override fun getSessionsForProfile(profileId: Long): Flow<List<Task>> {
        return taskDao.getSessionsForProfile(profileId).map { taskList ->
            taskList.map { it.toTask() }
        }
    }

    override suspend fun getActiveTaskId(): Long? {
        return taskDao.getActiveTaskId()
    }

    override suspend fun insertSegment(segment: Segment) {
        taskDao.insertSegment(segment.toSegmentEntity())
    }

    override suspend fun updateSegment(segment: Segment) {
        taskDao.updateSegment(segment.toSegmentEntity())
    }

    override suspend fun updateSegmentAndInsertNew(
        existing: Segment,
        new: Segment
    ) {
        taskDao.updateSegmentAndInsertNew(
            existing = existing.toSegmentEntity(),
            new = new.toSegmentEntity()
        )
    }

    override suspend fun insertMarker(marker: Marker) {
        taskDao.insertMarker(marker.toMarkerEntity())
    }
}