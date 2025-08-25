package com.wordco.clockworkandroid.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.wordco.clockworkandroid.ui.pages.EditTaskPage
import kotlinx.serialization.Serializable

// See https://github.com/android/nowinandroid modularized navigation


@Serializable
data class EditTaskRoute(val id: Long)

fun NavController.navigateToEdit(
    taskId: Long,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route = EditTaskRoute(taskId)) {
        navOptions()
    }
}

// See
// https://search.brave.com/search?q=default+viewmodel+extras&conversation=e96cd8b99dbedd699a77a6&summary=1


fun NavGraphBuilder.editTaskPage(
    onBackClick: () -> Unit
) {
    composable<EditTaskRoute> {
        entry ->
        val taskId = entry.toRoute<EditTaskRoute>().id

        val editTaskViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = EditTaskViewModel.Factory,
            extras = MutableCreationExtras(
                entry.defaultViewModelCreationExtras
            ).apply {
                set(EditTaskViewModel.Companion.TASK_ID_KEY, taskId)
            }
        )[EditTaskViewModel::class]

        EditTaskPage(
            onBackClick = onBackClick,
            editTaskViewModel = editTaskViewModel
        )
    }
}