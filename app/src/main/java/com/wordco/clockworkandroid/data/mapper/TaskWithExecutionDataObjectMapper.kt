package com.wordco.clockworkandroid.data.mapper

import com.wordco.clockworkandroid.data.local.entities.TaskWithExecutionDataObject
import com.wordco.clockworkandroid.domain.model.Task

fun TaskWithExecutionDataObject.toTask() : Task {
    val segments = segments.map { segmentEntity -> segmentEntity.toSegment() }

    return Task(
        taskId = taskEntity.taskId,
        name = taskEntity.name,
        dueDate = toOptionalInstant(taskEntity.dueDate),
        difficulty = taskEntity.difficulty,
        color = toColor(taskEntity.color),
        status = toTaskStatus(taskEntity.status),
        segments = segments,
        markers = markers.map { markerEntity -> markerEntity.toMarker() },
    )
}
