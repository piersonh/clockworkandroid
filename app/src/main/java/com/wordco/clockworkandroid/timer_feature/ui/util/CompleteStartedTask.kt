package com.wordco.clockworkandroid.timer_feature.ui.util

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import java.time.Duration
import java.time.Instant

suspend fun StartedTask.complete(taskRepository: TaskRepository) {
    val now = Instant.now()
    val lastSegment = segments.last().run {
        copy(duration = Duration.between(startTime, now))
    }

    val task = CompletedTask(
        taskId = taskId,
        profileId = profileId,
        name = name,
        dueDate = dueDate,
        difficulty = difficulty,
        color = color,
        userEstimate = userEstimate,
        segments = segments.map {
            if (it.segmentId == lastSegment.segmentId) {
                lastSegment
            } else {
                it
            }
        },
        markers = markers,
        appEstimate = appEstimate,
    )

    taskRepository.updateSegment(lastSegment)
    taskRepository.updateTask(task)
}