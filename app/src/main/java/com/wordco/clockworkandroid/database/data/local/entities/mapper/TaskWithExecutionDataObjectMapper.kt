package com.wordco.clockworkandroid.database.data.local.entities.mapper

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.database.data.local.entities.TaskWithExecutionDataObject
import com.wordco.clockworkandroid.database.data.util.toColor
import com.wordco.clockworkandroid.database.data.util.toOptionalDuration
import com.wordco.clockworkandroid.database.data.util.toOptionalInstant

fun TaskWithExecutionDataObject.toTask() : Task {
    return when (taskEntity.status) {
        0 -> NewTask(
            taskId = taskEntity.taskId,
            profileId = null,
            name = taskEntity.name,
            dueDate = toOptionalInstant(taskEntity.dueDate),
            difficulty = taskEntity.difficulty,
            color = toColor(taskEntity.color),
            userEstimate = toOptionalDuration(taskEntity.userEstimate)
        )
        1 -> StartedTask(
            taskId = taskEntity.taskId,
            profileId = null,
            name = taskEntity.name,
            dueDate = toOptionalInstant(taskEntity.dueDate),
            difficulty = taskEntity.difficulty,
            color = toColor(taskEntity.color),
            userEstimate = toOptionalDuration(taskEntity.userEstimate),
            segments = segments.map { segmentEntity -> segmentEntity.toSegment() },
            markers = markers.map { markerEntity -> markerEntity.toMarker() },
        )
        2 -> CompletedTask(
            taskId = taskEntity.taskId,
            profileId = null,
            name = taskEntity.name,
            dueDate = toOptionalInstant(taskEntity.dueDate),
            difficulty = taskEntity.difficulty,
            color = toColor(taskEntity.color),
            userEstimate = toOptionalDuration(taskEntity.userEstimate),
            segments = segments.map { segmentEntity -> segmentEntity.toSegment() },
            markers = markers.map { markerEntity -> markerEntity.toMarker() },
        )
        else -> error("Illegal task status read from database")
    }
}
