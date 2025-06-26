package com.wordco.clockworkandroid.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.wordco.clockworkandroid.ui.TaskViewModel
import com.wordco.clockworkandroid.ui.elements.FloatingNavButton
import com.wordco.clockworkandroid.ui.elements.StartedListItem
import com.wordco.clockworkandroid.ui.elements.UpcomingTaskUIListItem
import com.wordco.clockworkandroid.ui.theme.LATO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPage(
    controller: NavHostController,
    taskViewModel: TaskViewModel
) {
    val startedTaskList by taskViewModel.startedTaskList.observeAsState()
    val upcomingTaskList by taskViewModel.upcomingTaskList.observeAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Sessions", fontFamily = LATO, color = MaterialTheme.colorScheme.onPrimary)},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)

            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingNavButton("Add", controller, "Add")

        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(
                    PaddingValues(top = innerPadding.calculateTopPadding())
                )
                .background(color = MaterialTheme.colorScheme.primary)
        )
        {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .padding(5.dp)
                    .background(color = MaterialTheme.colorScheme.primary)
                    .fillMaxSize()
            ) {
                item {
                    Text(
                        "STARTED",
                        fontFamily = LATO,
                        fontSize = 25.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                startedTaskList?.let {
                    items(
                        startedTaskList!!,
                        key = { task -> task.taskId }
                    ) {
                        StartedListItem(
                            it,
                            Modifier
                                .fillMaxWidth()
                                .clip(shape = RoundedCornerShape(10.dp))
                                .background(color = MaterialTheme.colorScheme.primaryContainer)
                                .height(100.dp)
                                // TODO: Remove curtask and make routing per task
                                .clickable(onClick = {
                                    taskViewModel.onTaskClick(it.taskId)
                                    controller.navigate("Timer")
                                })
                        )
                    }
                }


                item {
                    Text(
                        "UPCOMING",
                        fontFamily = LATO,
                        fontSize = 25.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                upcomingTaskList?.let {
                    items(
                        upcomingTaskList!!,
                        key = { task -> task.taskId }
                    ) {
                        UpcomingTaskUIListItem(
                            it,
                            Modifier
                                .fillMaxWidth()
                                .clip(shape = RoundedCornerShape(10.dp))
                                .background(color = MaterialTheme.colorScheme.primaryContainer)
                                .height(100.dp)
                                // TODO: Remove curtask and make routing per task
                                .clickable(onClick = {
                                    taskViewModel.onTaskClick(it.taskId)
                                    controller.navigate("Timer")
                                })
                        )
                    }
                }

            }
        }
    }
}
