package com.wordco.clockworkandroid.core.domain.util

import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeSessionRepository(
    initialValues: List<Task>,
) : TaskRepository {

    private val _sessions = MutableStateFlow(initialValues)

    companion object {
        private lateinit var instance: FakeSessionRepository

        fun factory() : FakeSessionRepository {
            if (!::instance.isInitialized) {
                instance = FakeSessionRepository(DummyData.SESSIONS)
            }
            return instance
        }
    }

    override suspend fun insertTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun insertNewTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTask(task: Task) {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override suspend fun getActiveTask(): Flow<StartedTask>? {
        TODO("Not yet implemented")
    }

    override suspend fun insertSegment(segment: Segment) {
        TODO("Not yet implemented")
    }

    override suspend fun updateSegment(segment: Segment) {
        TODO("Not yet implemented")
    }

    override suspend fun updateSegmentAndInsertNew(
        existing: Segment,
        new: Segment
    ) {
        TODO("Not yet implemented")
    }
}