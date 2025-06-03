package com.wordco.clockworkandroid

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wordco.clockworkandroid.data.local.AppDatabase
import com.wordco.clockworkandroid.data.repository.TaskRepositoryImpl
import com.wordco.clockworkandroid.domain.model.Timer
import com.wordco.clockworkandroid.ui.TaskViewModel
import com.wordco.clockworkandroid.ui.pages.ListPage
import com.wordco.clockworkandroid.ui.pages.NewTaskPage
import com.wordco.clockworkandroid.ui.pages.TaskCompletionPage
import com.wordco.clockworkandroid.ui.pages.TimerPage


class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(applicationContext)
        val taskDao = db.taskDao()
        val taskRepository = TaskRepositoryImpl(taskDao)
        val taskViewModel = TaskViewModel(taskRepository)

        //taskRegistryViewModel.insertTasks(*TASKS.toTypedArray())


        enableEdgeToEdge()  // FIXME we probably do not want this
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "List"
            ) {
                composable(route = "List") {
                    ListPage(navController, taskViewModel)
                }
                composable(
                    route = "Add",
                    enterTransition = {
                        slideIntoContainer(
                            animationSpec = tween(150, easing = LinearEasing),
                            towards = AnimatedContentTransitionScope.SlideDirection.Up
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            animationSpec = tween(150, easing = LinearEasing),
                            towards = AnimatedContentTransitionScope.SlideDirection.Up
                        )
                    }
                )
                {
                    NewTaskPage(navController)
                }
                composable(route = "Timer") {
                    TimerPage(Timer(), navController = navController)
                }
                composable(route = "TaskCompletionPage") {
                    TaskCompletionPage()
                }
            }
        }
    }
}
