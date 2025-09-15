package com.wordco.clockworkandroid.edit_session_feature.ui

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
import com.wordco.clockworkandroid.timer_feature.ui.TimerViewModel
import kotlinx.serialization.Serializable

// See https://github.com/android/nowinandroid modularized navigation


@Serializable
data class CreateNewTaskRoute(val withProfile: Long?)

fun NavController.navigateToCreateNewTask(
    withProfile: Long? = null,
    navOptions: NavOptionsBuilder.() -> Unit = { launchSingleTop = true }
) {
    navigate(route = CreateNewTaskRoute(withProfile)) {
        navOptions()
    }
}

// See
// https://search.brave.com/search?q=default+viewmodel+extras&conversation=e96cd8b99dbedd699a77a6&summary=1


fun NavGraphBuilder.createNewTaskPage(
    onBackClick: () -> Unit
) {
    composable<CreateNewTaskRoute>(
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

        val profileId = entry.toRoute<CreateNewTaskRoute>().withProfile

        val createNewTaskViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = CreateNewTaskViewModel.Companion.Factory,
            extras = MutableCreationExtras(
                entry.defaultViewModelCreationExtras
            ).apply {
                set(CreateNewTaskViewModel.Companion.PROFILE_ID_KEY, profileId)
            }
        )[CreateNewTaskViewModel::class]


        CreateNewTaskPage(
            onBackClick = onBackClick,
            viewModel = createNewTaskViewModel,
            skipProfilePicker = profileId != null,
        )
    }
}