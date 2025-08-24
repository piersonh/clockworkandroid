package com.wordco.clockworkandroid.ui.mapper

import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.ui.SuspendedTaskListItem

fun Task.toSuspendedTaskListItem() : SuspendedTaskListItem {
    return SuspendedTaskListItem(
        taskId = taskId,
        name = name,
        color = color,
        workTime = workTime,
        breakTime = breakTime
    )
}