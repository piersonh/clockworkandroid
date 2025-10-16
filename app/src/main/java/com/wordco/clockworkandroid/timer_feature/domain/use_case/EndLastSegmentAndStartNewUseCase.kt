package com.wordco.clockworkandroid.timer_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import java.time.Duration
import java.time.Instant

class EndLastSegmentAndStartNewUseCase(
    private val sessionRepository: TaskRepository
) {
    suspend operator fun invoke(
        session: StartedTask,
        type: Segment.Type,
        now: Instant,
    ) {
        val lastSegment = session.segments.last().run {
            copy(duration = Duration.between(startTime, now))
        }
        val newSegment = Segment(
            segmentId = 0,
            taskId = session.taskId,
            startTime = now,
            duration = null,
            type = type
        )
        sessionRepository.updateSegmentAndInsertNew(
            existing = lastSegment,
            new = newSegment
        )
    }
}