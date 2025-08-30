package com.wordco.clockworkandroid.database.data.local.entities.mapper

import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.database.data.local.entities.TaskEntity
import com.wordco.clockworkandroid.database.data.util.fromColor
import com.wordco.clockworkandroid.database.data.util.fromOptionalInstant
import com.wordco.clockworkandroid.database.data.util.fromTaskStatus

fun Task.toTaskEntity() : TaskEntity {
    return TaskEntity(
        taskId = taskId,
        name = name,
        dueDate = fromOptionalInstant(dueDate),
        difficulty = difficulty,
        color = fromColor(color),
        status = fromTaskStatus(this)
    )
}