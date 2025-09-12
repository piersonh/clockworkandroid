package com.wordco.clockworkandroid.session_list_feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.core.ui.composables.NavBar
import com.wordco.clockworkandroid.core.ui.composables.PlusImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.FAKE_TOP_LEVEL_DESTINATIONS
import com.wordco.clockworkandroid.session_list_feature.ui.composables.ActiveTaskUiItem
import com.wordco.clockworkandroid.session_list_feature.ui.composables.StartedListItem
import com.wordco.clockworkandroid.session_list_feature.ui.composables.UpcomingTaskUIListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.SuspendedTaskListItem
import java.time.Duration

@Composable
fun TaskListPage(
    taskListViewModel: TaskListViewModel,
    navBar: @Composable () -> Unit,
    onTaskClick: (Long) -> Unit,
    onCreateNewTaskClick: () -> Unit,
) {
    val uiState by taskListViewModel.uiState.collectAsStateWithLifecycle()

    TaskListPage(
        uiState = uiState,
        navBar = navBar,
        onTaskClick = onTaskClick,
        onCreateNewTaskClick = onCreateNewTaskClick,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskListPage(
    uiState: TaskListUiState,
    navBar: @Composable () -> Unit,
    onTaskClick: (Long) -> Unit,
    onCreateNewTaskClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Task Sessions",
                        fontFamily = LATO,
                    )
                },
                actions = {
                    IconButton(
                        onClick = onCreateNewTaskClick
                    ) {
                        PlusImage(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxSize()
                        )
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
            )
        },
        bottomBar = {
            navBar()
            //TaskBottomBar(onCreateNewTaskClick)
        },
        modifier = Modifier.fillMaxSize()
    ) {
        innerPadding ->

        Box(
            modifier = Modifier
                .padding(
                    PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding(),
                    )
                )
                .background(color = MaterialTheme.colorScheme.primary)
        )
        {
            when (uiState) {
                is TaskListUiState.Retrieved -> TaskList(
                    uiState,
                    onTaskClick = onTaskClick,
                )
                TaskListUiState.Retrieving -> Text("Loading...")
            }
        }
    }
}


@Composable
private fun TaskList(
    uiState: TaskListUiState.Retrieved,
    onTaskClick: (Long) -> Unit,
) {
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

        if (uiState is TaskListUiState.TimerActive) {
            item {
                ActiveTaskUiItem(
                    task = uiState.activeTask,
                    modifier = Modifier.fillMaxWidth()
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .height(100.dp)
                        .clickable(onClick = { onTaskClick(uiState.activeTask.taskId) })
                )
            }
        }


        items(
            uiState.suspendedTasks,
            key = { task -> task.taskId }
        ) {
            StartedListItem(
                it,
                Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .height(100.dp)
                    .clickable(onClick = { onTaskClick(it.taskId) })
            )
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

        items(
            uiState.newTasks,
            key = { task -> task.taskId }
        ) {
            UpcomingTaskUIListItem(
                it,
                Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .height(100.dp)
                    .clickable(onClick = {onTaskClick(it.taskId)})
            )
        }

    }
}


@Preview
@Composable
private fun TaskListPagePreview() {
    ClockworkTheme {
        TaskListPage(
            uiState = TaskListUiState.TimerDormant(
                newTasks = listOf(),
                suspendedTasks = listOf(
                    SuspendedTaskListItem(
                        taskId = 0,
                        name = "Awooga",
                        color = Color(40, 50, 160),
                        workTime = Duration.ZERO,
                        breakTime = Duration.ZERO
                    )
                ),
            ),
            navBar = { NavBar(
                items = FAKE_TOP_LEVEL_DESTINATIONS,
                currentDestination = Unit::class,
                navigateTo = {},
            ) },
            onTaskClick = {},
            onCreateNewTaskClick = {}
        )
    }
}