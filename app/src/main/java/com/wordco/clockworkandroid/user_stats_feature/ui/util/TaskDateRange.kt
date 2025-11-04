package com.wordco.clockworkandroid.user_stats_feature.ui.util

import com.wordco.clockworkandroid.user_stats_feature.ui.model.CompletedSessionListItem
import java.time.ZonedDateTime

fun TaskDateRange(uiState: List<CompletedSessionListItem>, numMonths: Long):
        List<CompletedSessionListItem> {

    val twoMonthsAgo = ZonedDateTime.now().minusMonths(numMonths).toInstant()

    val taskRangeList = uiState.toList()
        .filter { it.completedAt >= twoMonthsAgo}

    return taskRangeList
}