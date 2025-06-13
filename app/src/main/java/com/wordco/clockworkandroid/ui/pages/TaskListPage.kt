package com.wordco.clockworkandroid.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.wordco.clockworkandroid.ui.TaskViewModel
import com.wordco.clockworkandroid.ui.theme.LATO
import com.wordco.clockworkandroid.ui.elements.FloatingNavButton
import com.wordco.clockworkandroid.ui.elements.StartedListItem
import com.wordco.clockworkandroid.ui.elements.TaskList
import com.wordco.clockworkandroid.ui.elements.UpcomingTaskUIListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPage (
    controller: NavHostController,
    taskViewModel: TaskViewModel
) = Scaffold(
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
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .padding(5.dp)
                .background(color = Color.DarkGray)
                .fillMaxSize()
        ) {
            item {
                Text(
                    "STARTED",
                    fontFamily = LATO,
                    fontSize = 25.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            items(
                taskViewModel.startedTaskList,
                key={task -> task.taskId}
            ) {
                StartedListItem(it)
            }

            item {
                Text(
                    "UPCOMING",
                    fontFamily = LATO,
                    fontSize = 25.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            items(
                taskViewModel.upcomingTaskList,
                key={task -> task.taskId}
            ) {
                UpcomingTaskUIListItem(it)
            }
        }
    }
}