package com.wordco.clockworkandroid.core.data.fake

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.Marker
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicLong

class FakeSessionRepository(
    initialValues: List<Task> = emptyList()
) : TaskRepository {

    private val _sessions = MutableStateFlow<Map<Long, Task>>(emptyMap())

    private val nextTaskId = AtomicLong(1L)
    private val nextSegmentId = AtomicLong(1L)
    private val nextMarkerId = AtomicLong(1L)

    init {
        _sessions.value = initialValues.associateBy { it.taskId }
        val maxTaskId = initialValues.maxOfOrNull { it.taskId } ?: 0L
        nextTaskId.set(maxTaskId + 1)

        val maxSegmentId = initialValues
            .filterIsInstance<Task.HasExecutionData>()
            .flatMap { it.segments }
            .maxOfOrNull { it.segmentId } ?: 0L
        nextSegmentId.set(maxSegmentId + 1)

        val maxMarkerId = initialValues
            .filterIsInstance<Task.HasExecutionData>()
            .flatMap { it.markers }
            .maxOfOrNull { it.markerId } ?: 0L
        nextMarkerId.set(maxMarkerId + 1)
    }


    override suspend fun insertNewTask(task: Task): Long {
        if (task.taskId != 0L) {
            error("new database entries must have an id of 0")
        }

        val newId = nextTaskId.getAndIncrement()

        val newTask = when (task) {
            is NewTask -> task.copy(taskId = newId)
            is CompletedTask -> task.copy(taskId = newId)
            is StartedTask -> task.copy(taskId = newId)
        }

        _sessions.update { currentMap ->
            currentMap + (newId to newTask)
        }
        return newId
    }

    override suspend fun updateTask(task: Task) {
        _sessions.update { currentMap ->
            if (currentMap.containsKey(task.taskId)) {
                currentMap + (task.taskId to task)
            } else {
                currentMap
            }
        }
    }

    override suspend fun deleteTask(id: Long) {
        _sessions.update { currentMap ->
            currentMap - id
        }
    }

    override fun getTask(taskId: Long): Flow<Task> {
        return _sessions.mapNotNull { map ->
            map[taskId]
        }
    }

    override fun getTasks(): Flow<List<Task>> {
        return _sessions.map { it.values.toList() }
    }

    override fun getTodoTasks(): Flow<List<Task.Todo>> {
        return _sessions.map { map ->
            map.values.filterIsInstance<Task.Todo>()
        }
    }

    override fun getCompletedTasks(): Flow<List<CompletedTask>> {
        return _sessions.map { map ->
            map.values.filterIsInstance<CompletedTask>()
        }
    }

    override fun getSessionsForProfile(profileId: Long): Flow<List<Task>> {
        return _sessions.map { map ->
            map.values.filter { it.profileId == profileId }
        }
    }

    override fun getCompletedSessionsForProfile(profileId: Long): Flow<List<CompletedTask>> {
        return _sessions.map { map ->
            map.values.filter { it.profileId == profileId }
                .filterIsInstance<CompletedTask>()
        }
    }

    override suspend fun getActiveTaskId(): Long? {
        return _sessions.value.values.firstOrNull {
            it is StartedTask && it.segments.last().type == Segment.Type.WORK
        }?.taskId
    }

    override suspend fun insertSegment(segment: Segment) {
        if (segment.segmentId != 0L) {
            error("new database entries must have an id of 0")
        }

        val newId = nextSegmentId.getAndIncrement()
        val newSegment = segment.copy(segmentId = newId)

        _sessions.update { currentMap ->
            val currentTask = currentMap[segment.taskId]
                ?: error("Task not found for segment")

            val updatedTask = when (currentTask) {
                is NewTask -> StartedTask(
                    taskId = currentTask.taskId,
                    name = currentTask.name,
                    dueDate = currentTask.dueDate,
                    difficulty = currentTask.difficulty,
                    color = currentTask.color,
                    userEstimate = currentTask.userEstimate,
                    segments = listOf(newSegment),
                    markers = emptyList(),
                    profileId = currentTask.profileId,
                    appEstimate = currentTask.appEstimate,
                )
                is CompletedTask -> currentTask.copy(
                    segments = currentTask.segments + newSegment
                )
                is StartedTask -> currentTask.copy(
                    segments = currentTask.segments + newSegment
                )
            }

            currentMap + (updatedTask.taskId to updatedTask)
        }
    }

    override suspend fun updateSegment(segment: Segment) {
        _sessions.update { currentMap ->
            val currentTask = currentMap[segment.taskId]
                ?: error("Task not found for segment")

            val executableTask = currentTask as? Task.HasExecutionData
                ?: error("New Tasks cannot have segments")

            val newSegments = executableTask.segments.map {
                if (it.segmentId == segment.segmentId) segment else it
            }

            val updatedTask = when (executableTask) {
                is CompletedTask -> executableTask.copy(segments = newSegments)
                is StartedTask -> executableTask.copy(segments = newSegments)
            }

            currentMap + (updatedTask.taskId to updatedTask)
        }
    }

    override suspend fun updateSegmentAndInsertNew(existing: Segment, new: Segment) {
        if (new.segmentId != 0L) {
            error("new database entries must have an id of 0")
        }

        val newId = nextSegmentId.getAndIncrement()
        val newSegmentWithId = new.copy(segmentId = newId)

        _sessions.update { currentMap ->
            val currentTask = currentMap[existing.taskId]
                ?: error("Task not found for segment")

            val executableTask = currentTask as? Task.HasExecutionData
                ?: error("New Tasks cannot have segments")

            val newSegments = executableTask.segments.map {
                if (it.segmentId == existing.segmentId) existing else it
            } + newSegmentWithId

            val updatedTask = when (executableTask) {
                is CompletedTask -> executableTask.copy(segments = newSegments)
                is StartedTask -> executableTask.copy(segments = newSegments)
            }

            currentMap + (updatedTask.taskId to updatedTask)
        }
    }

    override suspend fun insertMarker(marker: Marker) {
        if (marker.markerId != 0L) {
            error("new database entries must have an id of 0")
        }

        val newId = nextMarkerId.getAndIncrement()
        val newMarker = marker.copy(markerId = newId)

        _sessions.update { currentMap ->
            val currentTask = currentMap[marker.taskId]
                ?: error("Task not found for marker")

            val executableTask = currentTask as? Task.HasExecutionData
                ?: error("New Tasks cannot have markers")

            val updatedTask = when (executableTask) {
                is CompletedTask -> executableTask.copy(
                    markers = executableTask.markers + newMarker
                )
                is StartedTask -> executableTask.copy(
                    markers = executableTask.markers + newMarker
                )
            }

            currentMap + (updatedTask.taskId to updatedTask)
        }
    }
}