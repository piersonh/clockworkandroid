package com.wordco.clockworkandroid.core.domain.util

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.Marker
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeSessionRepository(
    initialValues: List<Task>,
) : TaskRepository {

    private val _sessions = MutableStateFlow(initialValues)

    companion object {
        private lateinit var instance: FakeSessionRepository

        fun factory(
            initialValues: List<Task> = emptyList()
        ) : FakeSessionRepository {
            if (!::instance.isInitialized) {
                instance = FakeSessionRepository(initialValues)
            }
            return instance
        }
    }

    override suspend fun insertTask(task: Task) {
        if (task.taskId != 0L) {
            error("new database entries must have an id of 0")
        }

        _sessions.update { sessions ->
            val newId = sessions.maxOfOrNull {
                it.taskId
            }?.plus(1) ?: 1

            sessions.plus(
                when (task) {
                    is NewTask -> task.copy(taskId = newId)
                    is CompletedTask -> task.copy(taskId = newId)
                    is StartedTask -> task.copy(taskId = newId)
                }
            )
        }
    }

    override suspend fun insertNewTask(task: Task) {
        if (task.taskId != 0L) {
            error("new database entries must have an id of 0")
        }

        _sessions.update { sessions ->
            val newId = sessions.maxOfOrNull {
                it.taskId
            }?.plus(1) ?: 1

            sessions.plus(
                when (task) {
                    is NewTask -> task.copy(taskId = newId)
                    is CompletedTask -> task.copy(taskId = newId)
                    is StartedTask -> task.copy(taskId = newId)
                }
            )
        }
    }

    override suspend fun updateTask(task: Task) {
        _sessions.update { sessions ->
            sessions.map { it ->
                if (it.taskId == task.taskId) {
                    task
                } else {
                    it
                }
            }
        }
    }

    override fun getTask(taskId: Long): Flow<Task> {
        return _sessions.map { sessions ->
            sessions.first { it.taskId == taskId }
        }
    }

    override fun getTasks(): Flow<List<Task>> {
        return _sessions.asStateFlow()
    }

    override fun getSessionsForProfile(profileId: Long): Flow<List<Task>> {
        return _sessions.map { sessions ->
            sessions.filter{ it.profileId == profileId }
        }
    }

    override suspend fun hasActiveTask(): Boolean {
        return _sessions.value.firstOrNull {
            it is StartedTask && it.segments.last().type == Segment.Type.WORK
        } != null
    }

    override suspend fun getActiveTask(): Flow<StartedTask>? {
        return _sessions.value.firstOrNull {
            it is StartedTask && it.segments.last().type == Segment.Type.WORK
        }?.let { activeSession ->
            _sessions.map { sessions ->
                sessions.first { it.taskId == activeSession.taskId } as StartedTask
            }
        }
    }

    override suspend fun insertSegment(segment: Segment) {
        if (segment.segmentId != 0L) {
            error("new database entries must have an id of 0")
        }

        _sessions.update { sessions ->
            val newId = sessions.maxOfOrNull { session ->
                (session as? Task.HasExecutionData)?.segments?.maxOfOrNull {
                    it.segmentId
                } ?: 0
            }?.plus(1) ?: 1

            sessions.map { session ->
                if (session.taskId == segment.taskId) {
                    when (session) {
                        is NewTask -> StartedTask(
                            taskId = session.taskId,
                            name = session.name,
                            dueDate = session.dueDate,
                            difficulty = session.difficulty,
                            color = session.color,
                            userEstimate = session.userEstimate,
                            segments = listOf(segment.copy(segmentId = newId)),
                            markers = emptyList(),
                            profileId = session.profileId,
                            appEstimate = session.appEstimate,
                        )

                        is CompletedTask -> session.copy(
                            segments = session.segments
                                .plus(segment.copy(segmentId = newId))
                        )

                        is StartedTask -> session.copy(
                            segments = session.segments
                                .plus(segment.copy(segmentId = newId))
                        )
                    }
                } else {
                    session
                }
            }
        }
    }

    override suspend fun updateSegment(segment: Segment) {
        _sessions.update { sessions ->
            sessions.map { session ->
                if (session.taskId == segment.taskId) {
                    (session as? Task.HasExecutionData)
                        ?: error("New Tasks cannot have segments")

                    val newSegments = session.segments.map {
                        if (it.segmentId == segment.segmentId) {
                            segment
                        } else {
                            it
                        }
                    }

                    when (session) {
                        is CompletedTask -> session.copy(
                            segments = newSegments
                        )
                        is StartedTask -> session.copy(
                            segments = newSegments
                        )
                    }
                } else {
                    session
                }
            }
        }
    }

    override suspend fun updateSegmentAndInsertNew(
        existing: Segment,
        new: Segment
    ) {
        if (new.segmentId != 0L) {
            error("new database entries must have an id of 0")
        }

        _sessions.update { sessions ->
            val newId = sessions.maxOfOrNull { session ->
                (session as? Task.HasExecutionData)?.segments?.maxOfOrNull {
                    it.segmentId
                } ?: 0
            }?.plus(1) ?: 1

            sessions.map { session ->
                if (session.taskId == existing.taskId) {
                    (session as? Task.HasExecutionData)
                        ?: error("New Tasks cannot have segments")

                    val newSegments = buildList {
                        session.segments.forEach {
                            if (it.segmentId == existing.segmentId) {
                                add(existing)
                            } else {
                                add(it)
                            }
                        }

                        add(new.copy(segmentId = newId))
                    }

                    when (session) {
                        is CompletedTask -> session.copy(
                            segments = newSegments
                        )
                        is StartedTask -> session.copy(
                            segments = newSegments
                        )
                    }
                } else {
                    session
                }
            }
        }
    }

    override suspend fun insertMarker(marker: Marker) {
        if (marker.markerId != 0L) {
            error("new database entries must have an id of 0")
        }

        _sessions.update { sessions ->
            val newId = sessions.maxOfOrNull { session ->
                (session as? Task.HasExecutionData)?.markers?.maxOfOrNull {
                    it.markerId
                } ?: 0
            }?.plus(1) ?: 1

            sessions.map { session ->
                if (session.taskId == marker.taskId) {
                    when (session) {
                        is NewTask -> error("Sessions must be started or completed to have markers")

                        is CompletedTask -> session.copy(
                            markers = session.markers
                                .plus(marker.copy(markerId = newId))
                        )

                        is StartedTask -> session.copy(
                            markers = session.markers
                                .plus(marker.copy(markerId = newId))
                        )
                    }
                } else {
                    session
                }
            }
        }
    }
}