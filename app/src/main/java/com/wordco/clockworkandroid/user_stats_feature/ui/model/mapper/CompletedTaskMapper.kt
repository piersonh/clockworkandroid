package com.wordco.clockworkandroid.user_stats_feature.ui.model.mapper

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.user_stats_feature.ui.model.CompletedTaskListItem

fun CompletedTask.toCompletedTaskListItem() : CompletedTaskListItem {
    return CompletedTaskListItem(
        taskId = taskId,
        name = name,
        color = color,
        workTime = workTime,
        breakTime = breakTime
    )
}