package com.wordco.clockworkandroid.session_list_feature.ui.model.mapper

import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.session_list_feature.ui.model.SuspendedTaskListItem

fun StartedTask.toSuspendedTaskListItem() : SuspendedTaskListItem {
    return SuspendedTaskListItem(
        taskId = taskId,
        name = name,
        color = color,
        workTime = workTime,
        breakTime = breakTime
    )
}