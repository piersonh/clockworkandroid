package com.wordco.clockworkandroid.user_stats_feature.ui

import com.wordco.clockworkandroid.user_stats_feature.ui.model.CompletedSessionListItem


sealed interface UserStatsUiState {

    data object Retrieving : UserStatsUiState

    data class Retrieved(
        val completedTasks: List<CompletedSessionListItem>
    ) : UserStatsUiState
}