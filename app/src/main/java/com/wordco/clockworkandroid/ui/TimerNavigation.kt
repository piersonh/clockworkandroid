package com.wordco.clockworkandroid.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.wordco.clockworkandroid.ui.pages.TimerPage
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
    onEditClick: (Long) -> Unit
) {
    composable<TimerRoute> {
        entry ->
        val taskId = entry.toRoute<TimerRoute>().id

        val timerViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = TimerViewModel.Factory,
            extras = MutableCreationExtras(
                entry.defaultViewModelCreationExtras
            ).apply {
                set(TimerViewModel.TASK_ID_KEY, taskId)
            }
        )[TimerViewModel::class]

        TimerPage(
            onBackClick = onBackClick,
            timerViewModel = timerViewModel,
            onEditClick = {onEditClick(taskId)}
        )
    }
}