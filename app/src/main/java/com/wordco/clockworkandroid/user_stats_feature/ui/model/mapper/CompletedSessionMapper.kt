package com.wordco.clockworkandroid.user_stats_feature.ui.model.mapper

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.user_stats_feature.ui.model.CompletedSessionListItem

fun CompletedTask.toCompletedSessionListItem() : CompletedSessionListItem {
    return CompletedSessionListItem(
        taskId = taskId,
        name = name,
        color = color,
        workTime = workTime,
        breakTime = breakTime,
        completedAt = completedAt
    )
}