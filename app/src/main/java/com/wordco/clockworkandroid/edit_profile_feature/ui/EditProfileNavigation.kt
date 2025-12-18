package com.wordco.clockworkandroid.edit_profile_feature.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class EditProfileRoute(val id: Long)

fun NavController.navigateToEditProfile(
    id: Long,
    navOptions: NavOptionsBuilder.() -> Unit = { launchSingleTop = true }
) {
    navigate(route = EditProfileRoute(id)) {
        navOptions()
    }
}


fun NavGraphBuilder.editProfilePage(
    onBackClick: () -> Unit
) {
    composable<EditProfileRoute>(
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it }, animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it }, animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it }, animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it }, animationSpec = tween(300)
            )
        }
    ) {
            entry ->
        val taskId = entry.toRoute<EditProfileRoute>().id

        val editProfileViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = EditProfileViewModel.Factory,
            extras = MutableCreationExtras(
                entry.defaultViewModelCreationExtras
            ).apply {
                set(EditProfileViewModel.Companion.PROFILE_ID_KEY, taskId)
            }
        )[EditProfileViewModel::class]

        EditProfilePage(
            onBackClick = onBackClick,
            viewModel = editProfileViewModel
        )
    }
}