package com.wordco.clockworkandroid.session_list_feature.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object TaskListRoute


fun NavGraphBuilder.taskListPage(
    navBar: @Composable () -> Unit,
    onTaskClick: (Long) -> Unit,
    onCreateNewTaskClick: () -> Unit,
) {
    composable<TaskListRoute> {
        entry ->

        val taskListViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = TaskListViewModel.Factory,
            extras = entry.defaultViewModelCreationExtras
        )[TaskListViewModel::class]

        TaskListPage(
            viewModel = taskListViewModel,
            navBar = navBar,
            onSessionClick = onTaskClick,
            onCreateNewSessionClick = onCreateNewTaskClick
        )
    }
}