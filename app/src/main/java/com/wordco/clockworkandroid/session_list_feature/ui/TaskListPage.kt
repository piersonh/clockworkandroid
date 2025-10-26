package com.wordco.clockworkandroid.session_list_feature.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.domain.model.AppEstimate
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.composables.AccentRectangleTextButton
import com.wordco.clockworkandroid.core.ui.composables.NavBar
import com.wordco.clockworkandroid.core.ui.composables.PlusImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews
import com.wordco.clockworkandroid.core.ui.util.FAKE_TOP_LEVEL_DESTINATIONS
import com.wordco.clockworkandroid.core.ui.util.dpScaledWith
import com.wordco.clockworkandroid.session_list_feature.ui.composables.ActiveTaskUiItem
import com.wordco.clockworkandroid.session_list_feature.ui.composables.StartedListItem
import com.wordco.clockworkandroid.session_list_feature.ui.composables.UpcomingTaskUIListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.ActiveTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.SuspendedTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.mapper.toNewTaskListItem
import java.time.Duration
import java.time.Instant

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
                        uiState.newTasks.isEmpty()
                                && uiState.suspendedTasks.isEmpty()
                                && uiState is TaskListUiState.TimerDormant
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
                BasicText(
                    text = "Create New Session",
                    style = TextStyle(
                        fontFamily = LATO,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondary,
                        textAlign = TextAlign.Center,
                    ),
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(
                        maxFontSize = 50.sp
                    )
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
        if (uiState is TaskListUiState.TimerActive) {
            item {
                ActiveTaskUiItem(
                    task = uiState.activeTask,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    onClick = { onTaskClick(uiState.activeTask.taskId) }
                )
            }
        } else if (uiState.suspendedTasks.isEmpty()) {
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
                            modifier = Modifier.height(33.dpScaledWith(25.sp)),
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


        items(
            uiState.suspendedTasks,
            key = { task -> task.taskId }
        ) {
            StartedListItem(
                it,
                MaterialTheme.colorScheme.primaryContainer,
                onClick = { onTaskClick(it.taskId) }
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
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = { onTaskClick(it.taskId) },
            )
        }

        item {
            Spacer(Modifier)
        }
    }
}


@AspectRatioPreviews
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
                        elapsedSeconds = 12345,
                        progressToEstimate = 0.5f,
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


@AspectRatioPreviews
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

@AspectRatioPreviews
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
                        elapsedSeconds = 12345,
                        progressToEstimate = 0.5f,
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


@AspectRatioPreviews
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

@Preview(name = "2. 37:18", showBackground = true, device = "spec:width=360dp,height=740dp,dpi=420")
@Composable
private fun PosterScreenShot() {
    ClockworkTheme {
        TaskListPage(
            uiState = TaskListUiState.TimerActive(
                newTasks = listOf(
                    NewTask(taskId=26, name="Implement Hash Table in C++", dueDate= Instant.parse("2025-10-30T03:59:00Z"), difficulty=4, color=Color(1.0f, 0.0f, 0.0f, 1.0f, ColorSpaces.Srgb), userEstimate=Duration.parse("PT2H30M"), profileId=5, appEstimate= AppEstimate(low = Duration.parse("PT1H41M15.193S"), high = Duration.parse("PT4H26M41.942S"))),
                    NewTask(taskId=27, name="Review Databases Ch4-8", dueDate=Instant.parse("2025-10-28T03:59:00Z"), difficulty=1, color=Color(0.0f, 0.0f, 1.0f, 1.0f, ColorSpaces.Srgb), userEstimate=Duration.parse("PT2H"), profileId=4, appEstimate=AppEstimate(low=Duration.parse("PT1H23M39.664S"), high=Duration.parse("PT3H28M4.395S"))),
                    NewTask(taskId=28, name="Read Database Ch8", dueDate=Instant.parse("2025-10-25T03:59:00Z"), difficulty=0, color=Color(1.0f, 1.0f, 0.0f, 1.0f, ColorSpaces.Srgb), userEstimate=Duration.parse("PT45M"), profileId=3, appEstimate=AppEstimate(low=Duration.parse("PT31M44.227S"), high=Duration.parse("PT1H18M58.212S"))),
                    NewTask(taskId=29, name="Update Portfolio Website", dueDate=null, difficulty=3, color=Color(0.0f, 1.0f, 0.0f, 1.0f, ColorSpaces.Srgb), userEstimate=Duration.parse("PT2H15M"), profileId=6, appEstimate=AppEstimate(low=Duration.parse("PT1H28M23.686S"), high=Duration.parse("PT3H58M49.817S"))),
                    NewTask(taskId=30, name="Calc HW5", dueDate=Instant.parse("2025-10-30T03:59:00Z"), difficulty=3, color=Color(0.0f, 1.0f, 0.0f, 1.0f, ColorSpaces.Srgb), userEstimate=Duration.parse("PT2H"), profileId=1, appEstimate=AppEstimate(low=Duration.parse("PT1H23M19.012S"), high=Duration.parse("PT3H26M45.394S"))),
                    NewTask(taskId=31, name="DSA Term Project Deliverable 2", dueDate=Instant.parse("2025-11-13T04:59:00Z"), difficulty=2, color=Color(1.0f, 0.0f, 1.0f, 1.0f, ColorSpaces.Srgb), userEstimate=Duration.parse("PT5H45M"), profileId=2, appEstimate=AppEstimate(low=Duration.parse("PT3H32M29.525S"), high=Duration.parse("PT11H42M13.613S"))),
                ).map { it.toNewTaskListItem() },
                suspendedTasks = emptyList(),
                activeTask = ActiveTaskListItem(
                    name = "Implement Linked List in Rust",
                    taskId = 32,
                    status = ActiveTaskListItem.Status.RUNNING,
                    color = Color(1.0f, 0.0f, 0.0f, 1.0f, ColorSpaces.Srgb),
                    elapsedSeconds = 1234,
                    currentSegmentElapsedSeconds = 321,
                    progressToEstimate = 0.38f,
                )
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
