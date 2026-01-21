package com.wordco.clockworkandroid.session_editor_feature.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.wordco.clockworkandroid.session_editor_feature.coordinator.SessionEditorMode
import com.wordco.clockworkandroid.session_editor_feature.coordinator.SessionEditorViewModel
import com.wordco.clockworkandroid.session_editor_feature.ui.main_form.SessionFormPage
import com.wordco.clockworkandroid.session_editor_feature.ui.main_form.SessionFormViewModel
import com.wordco.clockworkandroid.session_editor_feature.ui.profile_picker.ProfilePickerPage
import com.wordco.clockworkandroid.session_editor_feature.ui.profile_picker.ProfilePickerViewModel
import kotlinx.serialization.Serializable

/**
 * Clunky object definition to tell the editor sub-graph what to do.
 *
 * @property sessionId If absent -> Create Mode, else -> Edit Mode
 * @property profileId Only relevant to Create Mode.  If present -> show Profile Picker, else -> skip
 */
@Serializable
private data class SessionEditorGraphRoute(
    val sessionId: Long? = null,
    val profileId: Long? = null,
)

@Serializable
private object EditorDispatcherRoute
@Serializable
private object EditorMainFormRoute
@Serializable
private object EditorProfilePickerRoute


fun NavController.navigateToCreateSessionWithPicker(
    navOptions: NavOptionsBuilder.() -> Unit = { launchSingleTop = true }
) {
    navigate(SessionEditorGraphRoute()) {
        navOptions()
    }
}

fun NavController.navigateToEditSession(
    sessionId: Long,
    navOptions: NavOptionsBuilder.() -> Unit = { launchSingleTop = true }
) {
    navigate(SessionEditorGraphRoute(sessionId = sessionId)) {
        navOptions()
    }
}

fun NavController.navigateToCreateSession(
    profileId: Long,
    navOptions: NavOptionsBuilder.() -> Unit = { launchSingleTop = true }
) {
    navigate(SessionEditorGraphRoute(profileId = profileId)) {
        navOptions()
    }
}


fun NavGraphBuilder.sessionEditorGraph(
    navController: NavHostController,
    onNavigateToCreateProfile: () -> Unit,
) {
    navigation<SessionEditorGraphRoute>(
        startDestination = EditorDispatcherRoute
    ) {

        @Composable
        fun getSharedViewModel(currentEntry: NavBackStackEntry): SessionEditorViewModel {
            val graphEntry = remember(currentEntry) {
                navController.getBackStackEntry<SessionEditorGraphRoute>()
            }

            val route = graphEntry.toRoute<SessionEditorGraphRoute>()

            val editorMode = when (route.sessionId) {
                null -> SessionEditorMode.Create(route.profileId)
                else -> SessionEditorMode.Edit(route.sessionId)
            }

            return ViewModelProvider.create(
                store = graphEntry.viewModelStore,
                factory = SessionEditorViewModel.Factory,
                extras = MutableCreationExtras(graphEntry.defaultViewModelCreationExtras).apply {
                    set(SessionEditorViewModel.EDITOR_MODE_KEY, editorMode)
                }
            )[SessionEditorViewModel::class]
        }

        composable<EditorDispatcherRoute> { entry ->
            val viewModel = getSharedViewModel(entry)
            
            LaunchedEffect(Unit) {
                when (val mode = viewModel.sessionEditorMode) {
                    is SessionEditorMode.Create if mode.profileId == null -> {
                        navController.navigate(EditorProfilePickerRoute) {
                            popUpTo(EditorDispatcherRoute) { inclusive = true }
                        }
                    }
                    else -> {
                        navController.navigate(EditorMainFormRoute) {
                            popUpTo(EditorDispatcherRoute) { inclusive = true }
                        }
                    }
                }
            }
        }

        composable<EditorMainFormRoute> { entry ->
            val editorViewModel = getSharedViewModel(entry)
            
            val formViewModel = ViewModelProvider.create(
                store = entry.viewModelStore,
                factory = editorViewModel.MainFormFactory,
                extras = entry.defaultViewModelCreationExtras
            )[SessionFormViewModel::class]

            SessionFormPage(
                onBackClick = navController::popBackStack,
                viewModel = formViewModel,
                onNavigateToProfilePicker = {
                    navController.navigate(EditorProfilePickerRoute) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<EditorProfilePickerRoute> { entry ->
            val editorViewModel = getSharedViewModel(entry)

            val pickerViewModel = ViewModelProvider.create(
                store = entry.viewModelStore,
                factory = editorViewModel.ProfilePickerFactory,
                extras = entry.defaultViewModelCreationExtras
            )[ProfilePickerViewModel::class]

            ProfilePickerPage(
                viewModel = pickerViewModel,
                onBackClick = navController::popBackStack,
                onProfileSelected = {
                    navController.navigate(EditorMainFormRoute) {
                        popUpTo(EditorProfilePickerRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToCreateProfile = onNavigateToCreateProfile
            )
        }
    }
}