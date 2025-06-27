package com.wordco.clockworkandroid.ui

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class PageRoutes (
    val route : String,
    val navArguments : List<NamedNavArgument> = emptyList()
) {
    object TaskListPage : PageRoutes("List")

    object TimerPage : PageRoutes(
        route = "Timer/{taskId}",
        navArguments = listOf(
            navArgument("taskId") {
                type = NavType.LongType
            }
        )
    ) {
        fun createRoute(taskId: Long) = "Timer/${taskId}"
    }

    object TaskCompletionPage : PageRoutes("TaskCompletionPage")
    object NewTaskPage : PageRoutes("Add")
}