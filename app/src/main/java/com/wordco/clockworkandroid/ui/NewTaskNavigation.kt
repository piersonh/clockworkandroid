package com.wordco.clockworkandroid.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.wordco.clockworkandroid.ui.pages.NewTaskPage
import com.wordco.clockworkandroid.ui.pages.TimerPage
import kotlinx.serialization.Serializable

// See https://github.com/android/nowinandroid modularized navigation


@Serializable
data object NewTaskRoute

fun NavController.navigateToNewTask(
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route = NewTaskRoute) {
        navOptions()
    }
}

// See
// https://search.brave.com/search?q=default+viewmodel+extras&conversation=e96cd8b99dbedd699a77a6&summary=1


fun NavGraphBuilder.newTaskPage(
    onBackClick: () -> Unit
) {
    composable<NewTaskRoute> {
        entry ->
        val newTaskViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = NewTaskViewModel.Factory,
            extras = entry.defaultViewModelCreationExtras
        )[NewTaskViewModel::class]

        NewTaskPage(
            onBackClick = onBackClick,
            newTaskViewModel = newTaskViewModel
        )
    }
}