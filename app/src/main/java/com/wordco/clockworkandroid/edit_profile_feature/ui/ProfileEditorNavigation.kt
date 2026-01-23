package com.wordco.clockworkandroid.edit_profile_feature.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
private data object CreateProfileRoute

@Serializable
private data class EditProfileRoute(val id: Long)


fun NavController.navigateToCreateProfile(
    navOptions: NavOptionsBuilder.() -> Unit = { launchSingleTop = true }
) {
    navigate(route = CreateProfileRoute) {
        navOptions()
    }
}


fun NavController.navigateToEditProfile(
    id: Long,
    navOptions: NavOptionsBuilder.() -> Unit = { launchSingleTop = true }
) {
    navigate(route = EditProfileRoute(
        id
    )
    ) {
        navOptions()
    }
}


fun NavGraphBuilder.profileEditorPage(
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
    ) { entry ->
        
        val profileId = entry.toRoute<EditProfileRoute>().id

        val viewModel = createViewModel(
            entry = entry,
            formMode = ProfileEditorMode.Edit(
                profileId = profileId
            )
        )

        ProfileEditorPage(
            viewModel = viewModel,
            onBackClick = onBackClick,
        )
    }

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

        val viewModel = createViewModel(
            entry = entry,
            formMode = ProfileEditorMode.Create
        )

        ProfileEditorPage(
            viewModel = viewModel,
            onBackClick = onBackClick
        )
    }
}


@Composable
private fun createViewModel(
    entry: NavBackStackEntry,
    formMode: ProfileEditorMode
): ProfileEditorViewModel {
    return ViewModelProvider.create(
        store = entry.viewModelStore,
        factory = ProfileEditorViewModel.Factory,
        extras = MutableCreationExtras(entry.defaultViewModelCreationExtras).apply {
            set(ProfileEditorViewModel.FORM_MODE_KEY, formMode)
        }
    )[ProfileEditorViewModel::class]
}