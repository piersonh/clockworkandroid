package com.wordco.clockworkandroid.edit_profile_feature.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object CreateProfileRoute

fun NavController.navigateToCreateProfile(
    navOptions: NavOptionsBuilder.() -> Unit = { launchSingleTop = true }
) {
    navigate(route = CreateProfileRoute) {
        navOptions()
    }
}

// See
// https://search.brave.com/search?q=default+viewmodel+extras&conversation=e96cd8b99dbedd699a77a6&summary=1


fun NavGraphBuilder.createProfilePage(
    onBackClick: () -> Unit
) {
    composable<CreateProfileRoute>(
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
    ) { entry ->

        val createProfileViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = CreateProfileViewModel.Companion.Factory,
            extras = entry.defaultViewModelCreationExtras
        )[CreateProfileViewModel::class]

        CreateProfilePage(
            viewModel = createProfileViewModel,
            onBackClick = onBackClick,
        )
    }
}