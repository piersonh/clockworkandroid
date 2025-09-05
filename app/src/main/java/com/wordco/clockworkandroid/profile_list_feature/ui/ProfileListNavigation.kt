package com.wordco.clockworkandroid.profile_list_feature.ui

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object ProfileListRoute

fun NavController.navigateToProfileList(
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route = ProfileListRoute) {
        navOptions()
    }
}


fun NavGraphBuilder.taskListPage(
    onProfileClick: (Long) -> Unit,
    onCreateNewProfileClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    composable<ProfileListRoute> {
            entry ->

        val profileListViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = ProfileListViewModel.Companion.Factory,
            extras = entry.defaultViewModelCreationExtras
        )[ProfileListViewModel::class]

        ProfileListPage(
            profileListViewModel = profileListViewModel,
            onProfileClick = onProfileClick,
            onCreateNewProfileClick = onCreateNewProfileClick,
            onBackClick = onBackClick,
        )
    }
}