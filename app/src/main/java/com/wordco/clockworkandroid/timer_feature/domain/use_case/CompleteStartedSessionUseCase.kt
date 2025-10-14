package com.wordco.clockworkandroid.timer_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import java.time.Duration
import java.time.Instant

class CompleteStartedSessionUseCase(
    private val sessionRepository: TaskRepository
) {
    suspend operator fun invoke(
        session: StartedTask
    ) {
        val now = Instant.now()
        val lastSegment = session.segments.last().run {
            copy(duration = Duration.between(startTime, now))
        }

        val task = CompletedTask(
            taskId = session.taskId,
            profileId = session.profileId,
            name = session.name,
            dueDate = session.dueDate,
            difficulty = session.difficulty,
            color = session.color,
            userEstimate = session.userEstimate,
            segments = session.segments.map {
                if (it.segmentId == lastSegment.segmentId) {
                    lastSegment
                } else {
                    it
                }
            },
            markers = session.markers,
            appEstimate = session.appEstimate,
        )

        sessionRepository.updateSegment(lastSegment)
        sessionRepository.updateTask(task)
    }

}