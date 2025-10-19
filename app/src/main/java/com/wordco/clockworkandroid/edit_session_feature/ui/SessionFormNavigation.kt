package com.wordco.clockworkandroid.edit_session_feature.ui

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
data class CreateNewSessionRoute(val profileId: Long? = null)

@Serializable
data class EditSessionRoute(val sessionId: Long)


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

        SessionFormPage(
            onBackClick = onBackClick,
            onCreateProfileClick = onCreateNewProfileClick,
            viewModel = viewModel,
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
            onCreateProfileClick = onCreateNewProfileClick,
            viewModel = viewModel,
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