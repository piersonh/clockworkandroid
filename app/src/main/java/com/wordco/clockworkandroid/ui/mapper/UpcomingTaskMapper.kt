package com.wordco.clockworkandroid.ui.mapper

import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.ui.UpcomingTaskListItem
import java.time.Duration

fun Task.toUpcomingTaskListItem() : UpcomingTaskListItem {
    return UpcomingTaskListItem(
        taskId = taskId,
        name = name,
        dueDate = dueDate,
        difficulty = difficulty,
        color = color,
        userEstimate = Duration.ZERO,
        appEstimate = Duration.ZERO
    )
}