package com.wordco.clockworkandroid.user_stats_feature.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object UserStatsRoute

fun NavController.navigateToUserStats(
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route = UserStatsRoute) {
        navOptions()
    }
}


fun NavGraphBuilder.userStatsPage(
    navBar: @Composable () -> Unit,
    onCompletedSessionClick: (Long) -> Unit
) {
    composable<UserStatsRoute> {
        entry ->

        val userStatsViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = UserStatsViewModel.Companion.Factory,
            extras = entry.defaultViewModelCreationExtras
        )[UserStatsViewModel::class]

        UserStatsPage(
            viewModel = userStatsViewModel,
            navBar = navBar,
            onCompletedSessionClick = onCompletedSessionClick
        )
    }
}