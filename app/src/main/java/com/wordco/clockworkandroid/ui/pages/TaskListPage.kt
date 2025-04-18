package com.wordco.clockworkandroid.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.wordco.clockworkandroid.model.TASKS
import com.wordco.clockworkandroid.model.Task
import com.wordco.clockworkandroid.ui.LATO
import com.wordco.clockworkandroid.ui.elements.FloatingNavButton
import com.wordco.clockworkandroid.ui.elements.TaskList
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPage(controller: NavHostController, curTask : MutableState<Task>) = Scaffold(
    topBar = {
        TopAppBar(
            title = { Text("Task Sessions", fontFamily = LATO) }

        )
    },
    floatingActionButtonPosition = FabPosition.End,
    floatingActionButton = {
        FloatingNavButton("Add", controller, "Add")

    },
    modifier = Modifier.fillMaxSize()
) { innerPadding ->
    Box(
        modifier = Modifier.padding(
            PaddingValues(top = innerPadding.calculateTopPadding())
        )
    )
    {
        TaskList(TASKS, controller, curTask)
    }
}