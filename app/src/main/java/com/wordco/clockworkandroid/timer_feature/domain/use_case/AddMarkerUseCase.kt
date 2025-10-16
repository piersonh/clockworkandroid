package com.wordco.clockworkandroid.timer_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.Marker
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import java.time.Instant

class AddMarkerUseCase {
    suspend operator fun invoke(
        sessionRepository: TaskRepository,
        session: StartedTask,
        now: Instant,
    ) : String {
        val name = "Marker ${session.markers.size + 1}"

        sessionRepository.insertMarker(
            Marker(
                markerId = 0,
                taskId = session.taskId,
                startTime = now,
                label = name
            )
        )

        return name
    }
}