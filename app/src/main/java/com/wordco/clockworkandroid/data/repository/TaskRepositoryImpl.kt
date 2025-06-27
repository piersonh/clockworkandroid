package com.wordco.clockworkandroid.data.repository

import com.wordco.clockworkandroid.data.local.TaskDao
import com.wordco.clockworkandroid.data.mapper.toMarkerEntity
import com.wordco.clockworkandroid.data.mapper.toSegmentEntity
import com.wordco.clockworkandroid.data.mapper.toTask
import com.wordco.clockworkandroid.data.mapper.toTaskEntity
import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl (
    private val taskDao: TaskDao
) : TaskRepository {
    override suspend fun insertTask(task: Task) {
        taskDao.insertTask(task.toTaskEntity())
        taskDao.insertSegments(task.segments.map { segment -> segment.toSegmentEntity() })
        taskDao.insertMarkers(task.markers.map { marker -> marker.toMarkerEntity() })
    }

    override fun getTask(taskId: Long): Flow<Task> {
        return taskDao.getTaskWithExecutionData(taskId).map {
            it.toTask()
        }
    }

    override fun getTasks(): Flow<List<Task>> {
        return taskDao.getTasksWithExecutionData()
            .map { taskList ->
                taskList.map { it.toTask() }
            }
    }
}