package com.wordco.clockworkandroid.data.mapper

import com.wordco.clockworkandroid.data.local.entities.TaskEntity
import com.wordco.clockworkandroid.domain.model.Task

fun Task.toTaskEntity() : TaskEntity {
    return TaskEntity(
        taskId = taskId,
        name = name,
        dueDate = dueDate,
        difficulty = difficulty,
        color = color,
        status = status
    )
}