package com.wordco.clockworkandroid

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.wordco.clockworkandroid.core.ui.composables.NavBar
import com.wordco.clockworkandroid.core.ui.model.TopLevelDestination
import com.wordco.clockworkandroid.edit_profile_feature.ui.createProfilePage
import com.wordco.clockworkandroid.edit_profile_feature.ui.editProfilePage
import com.wordco.clockworkandroid.edit_profile_feature.ui.navigateToCreateProfile
import com.wordco.clockworkandroid.edit_profile_feature.ui.navigateToEditProfile
import com.wordco.clockworkandroid.edit_session_feature.ui.navigateToCreateNewSession
import com.wordco.clockworkandroid.edit_session_feature.ui.navigateToEditSession
import com.wordco.clockworkandroid.edit_session_feature.ui.sessionFormPage
import com.wordco.clockworkandroid.profile_list_feature.ui.ProfileListRoute
import com.wordco.clockworkandroid.profile_list_feature.ui.profileListPage
import com.wordco.clockworkandroid.profile_session_list_feature.ui.navigateToProfileSessionList
import com.wordco.clockworkandroid.profile_session_list_feature.ui.profileSessionListPage
import com.wordco.clockworkandroid.session_completion_feature.ui.navigateToCompletion
import com.wordco.clockworkandroid.session_completion_feature.ui.taskCompletionPage
import com.wordco.clockworkandroid.session_list_feature.ui.TaskListRoute
import com.wordco.clockworkandroid.session_list_feature.ui.taskListPage
import com.wordco.clockworkandroid.timer_feature.ui.TimerRoute
import com.wordco.clockworkandroid.timer_feature.ui.navigateToTimer
import com.wordco.clockworkandroid.timer_feature.ui.timerPage
import com.wordco.clockworkandroid.user_stats_feature.ui.UserStatsRoute
import com.wordco.clockworkandroid.user_stats_feature.ui.userStatsPage

val topLevelDestinations = listOf(
    TopLevelDestination(
        route = UserStatsRoute,
        icon = R.drawable.user,
        label = "Statistics",
    ),
    TopLevelDestination(
        route = TaskListRoute,
        icon = R.drawable.todo_list,
        label = "To-Do List",
    ),
    TopLevelDestination(
        route = ProfileListRoute,
        icon = R.drawable.fanned_cards,
        label = "Profiles",
    ),

)

@Composable
fun NavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = TaskListRoute,
        modifier = modifier,
        enterTransition = {
            fadeIn(
                initialAlpha = 1f
            )
        },
        exitTransition = {
            fadeOut(
                targetAlpha = 1f
            )
        },
    ) {
        val navBar = @Composable { currentDestination: Any ->
            NavBar(
                items = topLevelDestinations,
                currentDestination = currentDestination,
                navigateTo = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
//                    val navOptions: NavOptionsBuilder.() -> Unit = {
//                        popUpTo(navController.graph.findStartDestination().id) {
//                            saveState = true
//                        }
//                        launchSingleTop = true
//                        restoreState = true
//                    }
//
//                    when (route) {
//                        TaskListRoute -> navController.navigateToTaskList(navOptions)
//                        ProfileListRoute -> navController.navigateToProfileList(navOptions)
//                    }
                }
            )
        }

        sessionFormPage(
            onBackClick = navController::popBackStack,
            onCreateNewProfileClick = navController::navigateToCreateProfile,
        )

        taskListPage(
            navBar = { navBar(TaskListRoute) },
            onTaskClick = navController::navigateToTimer,
            onCreateNewTaskClick = navController::navigateToCreateNewSession,
        )


        timerPage(
            onBackClick = navController::popBackStack,
            onFinishClick = { sessionId ->
                navController.navigateToCompletion(sessionId) {
                    popUpTo(route = TimerRoute(sessionId)) {
                        inclusive = true
                    }
                }
            },
            onEditClick = navController::navigateToEditSession,
        )

        profileListPage(
            navBar = { navBar(ProfileListRoute) },
            onProfileClick = navController::navigateToProfileSessionList,
            onCreateNewProfileClick = navController::navigateToCreateProfile,
        )

        createProfilePage(
            onBackClick = navController::popBackStack
        )

        editProfilePage(
            onBackClick = navController::popBackStack
        )

        profileSessionListPage(
            onBackClick = navController::popBackStack,
            onEditClick = navController::navigateToEditProfile,
            onSessionClick = navController::navigateToTimer,
            onCreateNewSessionClick = navController::navigateToCreateNewSession,
            onCompletedSessionClick = navController::navigateToCompletion,
            navBar = { navBar(ProfileListRoute) },
        )

        taskCompletionPage(
            onBackClick = navController::popBackStack,
            onContinueClick = navController::popBackStack,
            onEditClick = navController::navigateToEditSession,
        )

        userStatsPage(
            navBar = { navBar(UserStatsRoute) },
            onCompletedSessionClick = navController::navigateToCompletion
        )
    }
}