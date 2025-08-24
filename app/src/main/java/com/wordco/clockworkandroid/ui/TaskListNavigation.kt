package com.wordco.clockworkandroid.ui

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.wordco.clockworkandroid.ui.pages.TaskListPage
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
    onNewTaskClick: () -> Unit,
) {
    composable<TaskListRoute> {
        entry ->

        val taskListViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = TaskListViewModel.Factory,
            extras = entry.defaultViewModelCreationExtras
        )[TaskListViewModel::class]

        TaskListPage(
            taskListViewModel = taskListViewModel,
            onTaskClick = onTaskClick,
            onNewTaskClick = onNewTaskClick
        )
    }
}