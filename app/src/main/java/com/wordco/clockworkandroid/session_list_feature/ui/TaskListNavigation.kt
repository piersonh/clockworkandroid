package com.wordco.clockworkandroid.session_list_feature.ui

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object TaskListRoute

// WE PROBABLY ONLY WANT TO GET TO THE TASK LIST BY POPPING TO THE ROOT
//fun NavController.navigateToTaskList(
//    navOptions: NavOptionsBuilder.() -> Unit = {}
//) {
//    navigate(route = TaskListRoute) {
//        navOptions()
//    }
//}


fun NavGraphBuilder.taskListPage(
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
            onTaskClick = onTaskClick,
            onCreateNewTaskClick = onCreateNewTaskClick
        )
    }
}