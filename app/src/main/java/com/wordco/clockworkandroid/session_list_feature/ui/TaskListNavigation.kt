package com.wordco.clockworkandroid.session_list_feature.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object TaskListRoute

fun NavController.navigateToTaskList(
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route = TaskListRoute) {
        navOptions()
    }
}


fun NavGraphBuilder.taskListPage(
    navBar: @Composable () -> Unit,
    onTaskClick: (Long) -> Unit,
    onCreateNewTaskClick: () -> Unit,
) {
    composable<TaskListRoute> {
        entry ->

        val taskListViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = TaskListViewModel.Companion.Factory,
            extras = entry.defaultViewModelCreationExtras
        )[TaskListViewModel::class]

        TaskListPage(
            taskListViewModel = taskListViewModel,
            navBar = navBar,
            onTaskClick = onTaskClick,
            onCreateNewTaskClick = onCreateNewTaskClick
        )
    }
}