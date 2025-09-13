package com.wordco.clockworkandroid.profile_session_list_feature.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class ProfileSessionListRoute(val id: Long)

fun NavController.navigateToProfileSessionList(
    profileId: Long,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route = ProfileSessionListRoute(profileId)) {
        navOptions()
    }
}


fun NavGraphBuilder.profileSessionListPage(
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onSessionClick: (Long) -> Unit,
    onCreateNewSessionClick: () -> Unit,
    navBar: @Composable () -> Unit,
) {
    composable<ProfileSessionListRoute> { entry ->

        val profileId = entry.toRoute<ProfileSessionListRoute>().id

        val profileSessionListViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = ProfileSessionListViewModel.Companion.Factory,
            extras = MutableCreationExtras(
                entry.defaultViewModelCreationExtras
            ).apply {
                set(ProfileSessionListViewModel.Companion.PROFILE_ID_KEY, profileId)
            }
        )[ProfileSessionListViewModel::class]

        ProfileSessionListPage(
            viewModel = profileSessionListViewModel,
            onBackClick = onBackClick,
            onEditClick = onEditClick,
            onSessionClick = onSessionClick,
            onCreateNewSessionClick = onCreateNewSessionClick,
            navBar = navBar,
        )
    }
}