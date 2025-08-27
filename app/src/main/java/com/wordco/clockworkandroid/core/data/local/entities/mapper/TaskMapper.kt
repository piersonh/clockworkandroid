package com.wordco.clockworkandroid.core.data.local.entities.mapper

import com.wordco.clockworkandroid.core.data.local.entities.TaskEntity
import com.wordco.clockworkandroid.core.domain.model.Task

fun Task.toTaskEntity() : TaskEntity {
    return TaskEntity(
        taskId = taskId,
        name = name,
        dueDate = fromOptionalInstant(dueDate),
        difficulty = difficulty,
        color = fromColor(color),
        status = getStatus()
    )
}