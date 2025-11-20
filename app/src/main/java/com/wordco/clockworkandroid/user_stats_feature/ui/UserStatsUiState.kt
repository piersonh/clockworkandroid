package com.wordco.clockworkandroid.user_stats_feature.ui

import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.user_stats_feature.ui.model.CompletedSessionListItem


sealed interface UserStatsUiState {

    data object Retrieving : UserStatsUiState

    data class Retrieved(
        val completedTasks: List<CompletedSessionListItem>,
        val accuracyChartData: List<Double>,
        val allProfiles: List<Profile>,
        val selectedProfileId: Long?,
    ) : UserStatsUiState
}