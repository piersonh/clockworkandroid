package com.wordco.clockworkandroid.data.mapper

import com.wordco.clockworkandroid.data.local.entities.TaskWithExecutionDataObject
import com.wordco.clockworkandroid.domain.model.CompletedTask
import com.wordco.clockworkandroid.domain.model.NewTask
import com.wordco.clockworkandroid.domain.model.StartedTask
import com.wordco.clockworkandroid.domain.model.Task

fun TaskWithExecutionDataObject.toTask() : Task {
    val segments = segments.map { segmentEntity -> segmentEntity.toSegment() }

    return when (taskEntity.status) {
        0 -> NewTask(
            taskId = taskEntity.taskId,
            name = taskEntity.name,
            dueDate = toOptionalInstant(taskEntity.dueDate),
            difficulty = taskEntity.difficulty,
            color = toColor(taskEntity.color),
            userEstimate = null,
        )
        1 -> StartedTask(
            taskId = taskEntity.taskId,
            name = taskEntity.name,
            dueDate = toOptionalInstant(taskEntity.dueDate),
            difficulty = taskEntity.difficulty,
            color = toColor(taskEntity.color),
            userEstimate = null,
            segments = segments,
            markers = markers.map { markerEntity -> markerEntity.toMarker() },
        )
        2 -> CompletedTask(
            taskId = taskEntity.taskId,
            name = taskEntity.name,
            dueDate = toOptionalInstant(taskEntity.dueDate),
            difficulty = taskEntity.difficulty,
            color = toColor(taskEntity.color),
            userEstimate = null,
            segments = segments,
            markers = markers.map { markerEntity -> markerEntity.toMarker() },
        )
        else -> throw RuntimeException("Illegal task status read from database")
    }
}
