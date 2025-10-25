package com.wordco.clockworkandroid.user_stats_feature.ui.model.mapper

import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.user_stats_feature.ui.model.CompletedSessionListItem
import java.time.Duration

fun CompletedTask.toCompletedSessionListItem() : CompletedSessionListItem {
    val totalTime = workTime.plus(breakTime)
    return CompletedSessionListItem(
        taskId = taskId,
        name = name,
        color = color,
        totalTime = totalTime,
        error = totalTime.minus(userEstimate?: Duration.ZERO), // FIXME
        completedAt = completedAt
    )
}