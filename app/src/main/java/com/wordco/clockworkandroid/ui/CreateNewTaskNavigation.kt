package com.wordco.clockworkandroid.ui

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.wordco.clockworkandroid.ui.pages.CreateNewTaskPage
import kotlinx.serialization.Serializable

// See https://github.com/android/nowinandroid modularized navigation


@Serializable
data object CreateNewTaskRoute

fun NavController.navigateToCreateNewTask(
    navOptions: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route = CreateNewTaskRoute) {
        navOptions()
    }
}

// See
// https://search.brave.com/search?q=default+viewmodel+extras&conversation=e96cd8b99dbedd699a77a6&summary=1


fun NavGraphBuilder.createNewTaskPage(
    onBackClick: () -> Unit
) {
    composable<CreateNewTaskRoute> {
        entry ->
        val createNewTaskViewModel = ViewModelProvider.create(
            store = entry.viewModelStore,
            factory = CreateNewTaskViewModel.Factory,
            extras = entry.defaultViewModelCreationExtras
        )[CreateNewTaskViewModel::class]

        CreateNewTaskPage(
            onBackClick = onBackClick,
            createNewTaskViewModel = createNewTaskViewModel
        )
    }
}