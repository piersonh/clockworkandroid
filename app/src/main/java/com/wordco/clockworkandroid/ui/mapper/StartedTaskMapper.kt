package com.wordco.clockworkandroid.ui.mapper

import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.ui.StartedTaskListItem

fun Task.toStartedTaskListItem() : StartedTaskListItem {
    return StartedTaskListItem(
        taskId = taskId,
        name = name,
        status = status,
        color = color,
        workTime = workTime,
        breakTime = breakTime
    )
}