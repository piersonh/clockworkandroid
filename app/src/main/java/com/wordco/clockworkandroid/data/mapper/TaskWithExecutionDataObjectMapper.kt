package com.wordco.clockworkandroid.data.mapper

import com.wordco.clockworkandroid.data.local.entities.TaskWithExecutionDataObject
import com.wordco.clockworkandroid.domain.model.Task

fun TaskWithExecutionDataObject.toTask() : Task {
    return Task(
        taskId = taskEntity.taskId,
        name = taskEntity.name,
        dueDate = taskEntity.dueDate,
        difficulty = taskEntity.difficulty,
        color = taskEntity.color,
        status = taskEntity.status,
        segments = segments.map { segmentEntity -> segmentEntity.toSegment() },
        markers = markers.map { markerEntity -> markerEntity.toMarker() }
    )
}