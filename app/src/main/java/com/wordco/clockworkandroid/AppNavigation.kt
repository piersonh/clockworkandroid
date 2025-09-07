package com.wordco.clockworkandroid

import androidx.annotation.DrawableRes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.wordco.clockworkandroid.edit_session_feature.ui.createNewTaskPage
import com.wordco.clockworkandroid.edit_session_feature.ui.editTaskPage
import com.wordco.clockworkandroid.edit_session_feature.ui.navigateToCreateNewTask
import com.wordco.clockworkandroid.edit_session_feature.ui.navigateToEdit
import com.wordco.clockworkandroid.profile_list_feature.ui.ProfileListRoute
import com.wordco.clockworkandroid.profile_list_feature.ui.navigateToProfileList
import com.wordco.clockworkandroid.profile_list_feature.ui.profileListPage
import com.wordco.clockworkandroid.session_list_feature.ui.TaskListRoute
import com.wordco.clockworkandroid.session_list_feature.ui.navigateToTaskList
import com.wordco.clockworkandroid.session_list_feature.ui.taskListPage
import com.wordco.clockworkandroid.timer_feature.ui.navigateToTimer
import com.wordco.clockworkandroid.timer_feature.ui.timerPage
import kotlin.reflect.KClass


enum class TopLevelDestination(
    val route: KClass<*>,
    @param:DrawableRes val icon: Int,
    val label: String,
    val navigate: (NavController) -> Unit,
) {

    USER_STATS(
        route = Unit::class,
        icon = R.drawable.user,
        label = "Statistics",
        navigate = { }
    ),
    SESSION_LIST(
        route = TaskListRoute::class,
        icon = R.drawable.cal,
        label = "Sessions",
        navigate = { it.navigateToTaskList { makeNavOptions(it) }}
    ),
    PROFILE_LIST(
        route = ProfileListRoute::class,
        icon = R.drawable.star,
        label = "Profiles",
        navigate = {it.navigateToProfileList{ makeNavOptions(it) }}
    ),
    ;

    companion object {
        private fun makeNavOptions(navController: NavController) : NavOptions {
            return navOptions {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}

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
        val navBar = @Composable { currentDestination: TopLevelDestination ->
            NavBar(
                items = TopLevelDestination.entries,
                navController = navController,
                currentDestination = currentDestination,
            )
        }

        taskListPage(
            navBar = { navBar(TopLevelDestination.SESSION_LIST) },
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

        profileListPage(
            navBar = { navBar(TopLevelDestination.PROFILE_LIST) },
            onProfileClick = { },
            onCreateNewProfileClick = { },
            onBackClick = {  },
        )
    }
}


@Composable
private fun NavBar(
    items: List<TopLevelDestination>,
    navController: NavController,
    currentDestination: TopLevelDestination,
) {
    NavigationBar {
        items.forEach { destination ->
            NavigationBarItem(
                selected = destination == currentDestination,
//                    navController.currentDestination?.hierarchy?.any {
//                    it.hasRoute(
//                        destination.route
//                    )
//                } ?: false,
                label = { Text(destination.label) },
                onClick = { destination.navigate(navController) },
                icon = {
                    Image(
                        painter = painterResource(destination.icon),
                        contentDescription = null,
                        modifier = Modifier.width(50.dp)
                        //modifier = Modifier.aspectRatio(0.7f),
                        //colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                    )
               },
            )
        }
    }
}