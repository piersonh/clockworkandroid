package com.wordco.clockworkandroid

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.wordco.clockworkandroid.ui.TaskListRoute
import com.wordco.clockworkandroid.ui.createNewTaskPage
import com.wordco.clockworkandroid.ui.editTaskPage
import com.wordco.clockworkandroid.ui.navigateToCreateNewTask
import com.wordco.clockworkandroid.ui.navigateToEdit
import com.wordco.clockworkandroid.ui.navigateToTimer
import com.wordco.clockworkandroid.ui.taskListPage
import com.wordco.clockworkandroid.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.ui.timerPage

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()  // FIXME we probably do not want this
        setContent {
            ClockworkTheme {
                remember { mutableStateOf(null) } // FIXME: what this is?
                val navController = rememberNavController()
                NavHost (
                    navController = navController,
                    startDestination = TaskListRoute,
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

                    createNewTaskPage (
                        onBackClick = navController::navigateUp,
                    )


                    timerPage(
                        onBackClick = navController::navigateUp,
                        onEditClick = navController::navigateToEdit
                    )

                    editTaskPage(
                        onBackClick = navController::navigateUp
                    )

                    //composable<PageRoutes.TaskComplete> {
                    //    TaskCompletionPage(navController, taskViewModel)
                    //}
                }
            }
        }
    }
}

