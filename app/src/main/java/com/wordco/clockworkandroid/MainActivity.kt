package com.wordco.clockworkandroid

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wordco.clockworkandroid.model.Task
import com.wordco.clockworkandroid.model.Timer
import com.wordco.clockworkandroid.ui.ClockworkTheme
import com.wordco.clockworkandroid.ui.pages.ListPage
import com.wordco.clockworkandroid.ui.pages.NewTaskPage
import com.wordco.clockworkandroid.ui.pages.TimerPage
import com.wordco.clockworkandroid.ui.pages.TaskCompletionPage

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClockworkTheme{
                val curTask = remember { mutableStateOf(Task()) }
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "List",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(300)
                        )
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(300)
                        )
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(300)
                        )
                    },

                    ) {
                    composable(route = "List") {
                        ListPage(navController, curTask)
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
                        TimerPage(Timer(), navController = navController, curTask)
                    }
                    composable(route = "TaskCompletionPage") {
                        TaskCompletionPage(navController, curTask)
                    }
                }
            }
        }
    }
}

