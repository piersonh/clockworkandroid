package com.wordco.clockworkandroid.core.data.local.entities.mapper

import com.wordco.clockworkandroid.core.data.local.entities.TaskWithExecutionDataObject
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task

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
        else -> error("Illegal task status read from database")
    }
}
