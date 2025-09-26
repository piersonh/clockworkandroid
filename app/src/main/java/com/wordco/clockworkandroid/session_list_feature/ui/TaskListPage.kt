package com.wordco.clockworkandroid.session_list_feature.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.composables.AccentRectangleTextButton
import com.wordco.clockworkandroid.core.ui.composables.NavBar
import com.wordco.clockworkandroid.core.ui.composables.PlusImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.FAKE_TOP_LEVEL_DESTINATIONS
import com.wordco.clockworkandroid.session_list_feature.ui.composables.ActiveTaskUiItem
import com.wordco.clockworkandroid.session_list_feature.ui.composables.StartedListItem
import com.wordco.clockworkandroid.session_list_feature.ui.composables.UpcomingTaskUIListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.SuspendedTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.mapper.toNewTaskListItem
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
                        fontWeight = FontWeight.Black,
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
        bottomBar = navBar,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.primary)
                .fillMaxSize()
        )
        {
            when (uiState) {
                is TaskListUiState.Retrieved if (
                        uiState.newTasks.isEmpty() && uiState.suspendedTasks.isEmpty()
                    ) -> EmptyTaskList(
                    onCreateNewTaskClick = onCreateNewTaskClick
                )

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
private fun EmptyTaskList(
    onCreateNewTaskClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 15.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        //Spacer(modifier = Modifier.weight(0.04f))

        Box (
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.todo_list),
                contentDescription = "To-Do List",
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(170.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )
        }

        //Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Your To-Do List is Empty!",
                fontFamily = LATO,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        //Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AccentRectangleTextButton(
                onClick = onCreateNewTaskClick,
            ) {
                Text(
                    text = "Create New Session",
                    fontFamily = LATO,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }
        }


        //Spacer(modifier = Modifier.weight(0.15f))
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
            .padding(horizontal = 5.dp)
            .background(color = MaterialTheme.colorScheme.primary)
            .fillMaxSize()
    ) {
        item {
            Spacer(Modifier)
        }

        item {
            Text(
                "STARTED",
                fontFamily = LATO,
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        if (uiState.suspendedTasks.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.stopwatch),
                            contentDescription = "Stopwatch",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.height(33.dp),
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            "No Started Tasks",
                            fontFamily = LATO,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }


        if (uiState is TaskListUiState.TimerActive) {
            item {
                ActiveTaskUiItem(
                    task = uiState.activeTask,
                    modifier = Modifier
                        .fillMaxWidth()
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
            Spacer(Modifier)
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

        if (uiState.newTasks.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Box (
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cal),
                            contentDescription = "Calendar",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.height(100.dp),
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No Upcoming Tasks",
                            fontFamily = LATO,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
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
                    .clickable(onClick = { onTaskClick(it.taskId) })
            )
        }

        item {
            Spacer(Modifier)
        }
    }
}


@Preview
@Composable
private fun TaskListPagePreview() {
    ClockworkTheme {
        TaskListPage(
            uiState = TaskListUiState.TimerDormant(
                newTasks = DummyData.SESSIONS
                    .filter { it is NewTask }
                    .map { (it as NewTask).toNewTaskListItem() },
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


@Preview
@Composable
private fun EmptyTaskListPagePreview() {
    ClockworkTheme {
        TaskListPage(
            uiState = TaskListUiState.TimerDormant(
                newTasks = emptyList(),
                suspendedTasks = emptyList(),
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

@Preview
@Composable
private fun NoNewTasksListPagePreview() {
    ClockworkTheme {
        TaskListPage(
            uiState = TaskListUiState.TimerDormant(
                newTasks = emptyList(),
                suspendedTasks = listOf(
                    SuspendedTaskListItem(
                        taskId = 1,
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


@Preview
@Composable
private fun NoStartedTasksListPagePreview() {
    ClockworkTheme {
        TaskListPage(
            uiState = TaskListUiState.TimerDormant(
                newTasks = DummyData.SESSIONS
                    .filter { it is NewTask }
                    .map { (it as NewTask).toNewTaskListItem() },
                suspendedTasks = emptyList(),
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