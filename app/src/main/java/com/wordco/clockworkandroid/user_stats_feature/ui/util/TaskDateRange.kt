package com.wordco.clockworkandroid.user_stats_feature.ui.util

import com.wordco.clockworkandroid.user_stats_feature.ui.UserStatsUiState
import com.wordco.clockworkandroid.user_stats_feature.ui.model.CompletedSessionListItem
import java.time.ZonedDateTime

fun TaskDateRange(uiState: UserStatsUiState.Retrieved, numMonths: Long):
        List<CompletedSessionListItem> {

    val twoMonthsAgo = ZonedDateTime.now().minusMonths(numMonths).toInstant()

    val taskRangeList = uiState.completedTasks.toList()
        .filter { it.completedAt >= twoMonthsAgo}

    return taskRangeList
}