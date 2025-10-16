package com.wordco.clockworkandroid.timer_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import java.time.Instant

class StartNewSessionUseCase(
    private val sessionRepository: TaskRepository,
) {
    suspend operator fun invoke(
        session: NewTask,
        now: Instant,
    ) : StartedTask {
        val segment = Segment(
            segmentId = 0,
            taskId = session.taskId,
            startTime = now,
            duration = null,
            type = Segment.Type.WORK
        )

        val task = StartedTask(
            taskId = session.taskId,
            profileId = session.profileId,
            name = session.name,
            dueDate = session.dueDate,
            difficulty = session.difficulty,
            color = session.color,
            userEstimate = session.userEstimate,
            segments = listOf(segment),
            markers = emptyList(),
            appEstimate = session.appEstimate,
        )

        sessionRepository.insertSegment(segment)
        sessionRepository.updateTask(task)

        return task
    }
}