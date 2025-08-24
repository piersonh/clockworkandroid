package com.wordco.clockworkandroid.ui.mapper

import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.ui.NewTaskListItem
import java.time.Duration

fun Task.toNewTaskListItem() : NewTaskListItem {
    return NewTaskListItem(
        taskId = taskId,
        name = name,
        dueDate = dueDate,
        difficulty = difficulty,
        color = color,
        userEstimate = Duration.ZERO,
        appEstimate = Duration.ZERO
    )
}