package com.wordco.clockworkandroid.ui.mapper

import com.wordco.clockworkandroid.domain.model.StartedTask
import com.wordco.clockworkandroid.ui.SuspendedTaskListItem

fun StartedTask.toSuspendedTaskListItem() : SuspendedTaskListItem {
    return SuspendedTaskListItem(
        taskId = taskId,
        name = name,
        color = color,
        workTime = workTime,
        breakTime = breakTime
    )
}