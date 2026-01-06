package com.wordco.clockworkandroid.edit_session_feature.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class CreateNewSessionRoute(val profileId: Long? = null)

@Serializable
data class EditSessionRoute(val sessionId: Long)

@Serializable
data class ProfilePickerRoute(val selectedProfileId: Long?) {
    companion object {
        // The key used to pass the result back
        const val RESULT_PROFILE_ID = "result_profile_id"
    }
}


fun NavController.navigateToCreateNewSession(
    profileId: Long? = null,
    navOptions: NavOptionsBuilder.() -> Unit = { launchSingleTop = true }
) {
    navigate(route = CreateNewSessionRoute(profileId = profileId)) {
        navOptions()
    }
}

fun NavController.navigateToEditSession(
    sessionId: Long,
    navOptions: NavOptionsBuilder.() -> Unit = { launchSingleTop = true }
) {
    navigate(route = EditSessionRoute(sessionId = sessionId)) {
        navOptions()
    }
}



fun NavGraphBuilder.sessionFormPage(
    onBackClick: () -> Unit,
    onCreateNewProfileClick: () -> Unit,
    onNavigateToProfilePicker: (Long?) -> Unit,
    onProfileSelected: (Long?) -> Unit,
) {
    composable<CreateNewSessionRoute>(
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

        val profileId = entry.toRoute<CreateNewSessionRoute>().profileId

        val viewModel = createViewModel(entry, SessionFormMode.Create(profileId = profileId))

        LaunchedEffect(entry) {
            entry.savedStateHandle.getLiveData<Long?>(ProfilePickerRoute.RESULT_PROFILE_ID)
                .asFlow().collect { println("Found: $it") }
        }
        SessionFormPage(
            onBackClick = onBackClick,
            viewModel = viewModel,
            onNavigateToProfilePicker = { id -> onNavigateToProfilePicker(id) },
        )
    }


    composable<EditSessionRoute>(
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

        val sessionId = entry.toRoute<EditSessionRoute>().sessionId

        val viewModel = createViewModel(entry, SessionFormMode.Edit(sessionId = sessionId))

        SessionFormPage(
            onBackClick = onBackClick,
            viewModel = viewModel,
            onNavigateToProfilePicker = { id -> onNavigateToProfilePicker(id) },
        )
    }

    composable<ProfilePickerRoute>(
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it }, animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it }, animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it }, animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { it }, animationSpec = tween(300)
            )
        }
    ) { entry ->
        val selectedProfileId = entry.toRoute<ProfilePickerRoute>().selectedProfileId

        val viewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = ProfilePickerViewModel.Factory,
            extras = MutableCreationExtras(entry.defaultViewModelCreationExtras).apply {
                set(ProfilePickerViewModel.SELECTED_PROFILE_KEY, selectedProfileId)
            }
        )[ProfilePickerViewModel::class]

        ProfilePickerPage(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onProfileSelected = { resultId ->
                onProfileSelected(resultId)
            },
            onNavigateToCreateProfile = onCreateNewProfileClick
        )
    }
}


@Composable
private fun createViewModel(
    entry: NavBackStackEntry,
    formMode: SessionFormMode
): SessionFormViewModel {
    return ViewModelProvider.create(
        store = entry.viewModelStore,
        factory = SessionFormViewModel.Factory,
        extras = MutableCreationExtras(entry.defaultViewModelCreationExtras).apply {
            set(SessionFormViewModel.FORM_MODE_KEY, formMode)
        }
    )[SessionFormViewModel::class]
}