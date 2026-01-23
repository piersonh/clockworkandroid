package com.wordco.clockworkandroid.session_report_feature.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
private data class SessionReportRoute(val id: Long)

fun NavController.navigateToSessionReport(
    sessionId: Long,
    navOptions: NavOptionsBuilder.() -> Unit = {launchSingleTop = true}
) {
    navigate(route = SessionReportRoute(sessionId)) {
        navOptions()
    }
}

fun NavGraphBuilder.sessionReportPage(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    onEditClick: (Long) -> Unit
) {
    composable<SessionReportRoute> (
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
        }
    ) { entry ->
        val sessionId = entry.toRoute<SessionReportRoute>().id

        val viewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = SessionReportViewModel.Factory,
            extras = MutableCreationExtras(
                entry.defaultViewModelCreationExtras
            ).apply {
                set(SessionReportViewModel.SESSION_ID_KEY, sessionId)
            }
        )[SessionReportViewModel::class]

        SessionReportPage(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onContinueClick = onContinueClick,
            onEditClick = { onEditClick(sessionId) },
        )
    }
}