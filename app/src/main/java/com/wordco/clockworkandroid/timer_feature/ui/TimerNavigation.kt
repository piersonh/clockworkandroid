package com.wordco.clockworkandroid.timer_feature.ui

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

// See https://github.com/android/nowinandroid modularized navigation


@Serializable
data class TimerRoute(val id: Long)

fun NavController.navigateToTimer(
    taskId: Long,
    navOptions: NavOptionsBuilder.() -> Unit = {launchSingleTop = true}
) {
    navigate(route = TimerRoute(taskId)) {
        navOptions()
    }
}

// See
// https://search.brave.com/search?q=default+viewmodel+extras&conversation=e96cd8b99dbedd699a77a6&summary=1


fun NavGraphBuilder.timerPage(
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onFinishClick: (Long) -> Unit
) {
    composable<TimerRoute>(
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

        val taskId = entry.toRoute<TimerRoute>().id

        val timerViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = TimerViewModel.Companion.Factory,
            extras = MutableCreationExtras(
                entry.defaultViewModelCreationExtras
            ).apply {
                set(TimerViewModel.Companion.TASK_ID_KEY, taskId)
            }
        )[TimerViewModel::class]

        TimerPage(
            onBackClick = onBackClick,
            timerViewModel = timerViewModel,
            onEditClick = {onEditClick(taskId)},
            onFinishClick = {onFinishClick(taskId)}
        )
    }
}