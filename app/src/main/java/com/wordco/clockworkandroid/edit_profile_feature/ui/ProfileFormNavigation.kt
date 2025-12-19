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
data object CreateProfileRoute

@Serializable
data class EditProfileRoute(val id: Long)


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


fun NavGraphBuilder.profileFormPage(
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
            formMode = ProfileFormMode.Edit(
                profileId = profileId
            )
        )

        ProfileFormPage(
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
            formMode = ProfileFormMode.Create
        )

        ProfileFormPage(
            viewModel = viewModel,
            onBackClick = onBackClick
        )
    }
}


@Composable
private fun createViewModel(
    entry: NavBackStackEntry,
    formMode: ProfileFormMode
): ProfileFormViewModel {
    return ViewModelProvider.create(
        store = entry.viewModelStore,
        factory = ProfileFormViewModel.Factory,
        extras = MutableCreationExtras(entry.defaultViewModelCreationExtras).apply {
            set(ProfileFormViewModel.FORM_MODE_KEY, formMode)
        }
    )[ProfileFormViewModel::class]
}