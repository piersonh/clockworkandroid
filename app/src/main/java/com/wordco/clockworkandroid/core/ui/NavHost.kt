package com.wordco.clockworkandroid.core.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.wordco.clockworkandroid.edit_session_feature.ui.createNewTaskPage
import com.wordco.clockworkandroid.edit_session_feature.ui.editTaskPage
import com.wordco.clockworkandroid.edit_session_feature.ui.navigateToCreateNewTask
import com.wordco.clockworkandroid.edit_session_feature.ui.navigateToEdit
import com.wordco.clockworkandroid.session_list_feature.ui.TaskListRoute
import com.wordco.clockworkandroid.session_list_feature.ui.taskListPage
import com.wordco.clockworkandroid.timer_feature.ui.navigateToTimer
import com.wordco.clockworkandroid.timer_feature.ui.timerPage

@Composable
fun NavHost(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = TaskListRoute,
        modifier = modifier,
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
        },
    ) {
        taskListPage(
            onTaskClick = navController::navigateToTimer,
            onCreateNewTaskClick = navController::navigateToCreateNewTask,
        )

        createNewTaskPage(
            onBackClick = navController::popBackStack,
        )


        timerPage(
            onBackClick = navController::popBackStack,
            onEditClick = navController::navigateToEdit
        )

        editTaskPage(
            onBackClick = navController::popBackStack
        )

        //composable<PageRoutes.TaskComplete> {
        //    TaskCompletionPage(navController, taskViewModel)
        //}
    }
}