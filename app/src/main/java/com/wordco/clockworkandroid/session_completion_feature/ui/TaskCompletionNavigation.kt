package com.wordco.clockworkandroid.session_completion_feature.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class CompletionRoute(val id: Long)

fun NavController.navigateToCompletion(
    taskId: Long,
    navOptions: NavOptionsBuilder.() -> Unit = {launchSingleTop = true}
) {
    navigate(route = CompletionRoute(taskId)) {
        navOptions()
    }
}

fun NavGraphBuilder.taskCompletionPage(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    composable<CompletionRoute> {
        entry ->
        val taskId = entry.toRoute<CompletionRoute>().id

        val taskCompletionViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = TaskCompletionViewModel.Companion.Factory,
            extras = MutableCreationExtras(
                entry.defaultViewModelCreationExtras
            ).apply {
                set(TaskCompletionViewModel.Companion.TASK_ID_KEY, taskId)
            }
        )[TaskCompletionViewModel::class]

        TaskCompletionPage(
            taskCompletionViewModel = taskCompletionViewModel,
            onBackClick = onBackClick,
            onContinueClick = onContinueClick
        )
    }
}