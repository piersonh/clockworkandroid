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
data class ProfileDetailsRoute(val id: Long)

fun NavController.navigateToProfileDetails(
    profileId: Long,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route = ProfileDetailsRoute(profileId)) {
        navOptions()
    }
}


fun NavGraphBuilder.profileDetailsPage(
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onSessionClick: (Long) -> Unit,
    onCreateNewSessionClick: (Long) -> Unit,
    onCompletedSessionClick: (Long) -> Unit,
    navBar: @Composable () -> Unit,
) {
    composable<ProfileDetailsRoute> { entry ->

        val profileId = entry.toRoute<ProfileDetailsRoute>().id

        val viewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = ProfileDetailsViewModel.Factory,
            extras = MutableCreationExtras(
                entry.defaultViewModelCreationExtras
            ).apply {
                set(ProfileDetailsViewModel.PROFILE_ID_KEY, profileId)
            }
        )[ProfileDetailsViewModel::class]

        ProfileDetailsPage(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onEditClick = { onEditClick(profileId) },
            onTodoSessionClick = onSessionClick,
            onCreateNewSessionClick = { onCreateNewSessionClick(profileId) },
            onCompletedSessionClick = onCompletedSessionClick,
            navBar = navBar,
        )
    }
}