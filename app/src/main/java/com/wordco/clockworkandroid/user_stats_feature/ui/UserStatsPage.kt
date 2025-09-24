package com.wordco.clockworkandroid.user_stats_feature.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.composables.NavBar
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.FAKE_TOP_LEVEL_DESTINATIONS
import com.wordco.clockworkandroid.user_stats_feature.ui.composables.CompletedTaskUIListItem
import com.wordco.clockworkandroid.user_stats_feature.ui.model.mapper.toCompletedSessionListItem

@Composable
fun UserStatsPage(
    viewModel: UserStatsViewModel,
    navBar: @Composable () -> Unit,
    onCompletedSessionClick: (Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UserStatsPage(
        uiState = uiState,
        navBar = navBar,
        onCompletedSessionClick = onCompletedSessionClick,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserStatsPage(
    uiState: UserStatsUiState,
    navBar: @Composable () -> Unit,
    onCompletedSessionClick: (Long) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Completed Session History",
                        fontFamily = LATO,
                        fontWeight = FontWeight.Black,
                    )
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
        )
        {
            when (uiState) {
                is UserStatsUiState.Retrieved if uiState.completedTasks.isEmpty() -> EmptyTaskList()
                is UserStatsUiState.Retrieved -> CompletedSessionList(
                    uiState,
                    onTaskClick = onCompletedSessionClick,
                )

                UserStatsUiState.Retrieving -> Text("Loading...")
            }
        }
    }
}

@Composable
private fun EmptyTaskList (
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 15.dp),
    ) {
        Spacer(modifier = Modifier.weight(0.04f))

        Box (
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.trophy),
                contentDescription = "To-Do List",
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(170.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No Completed Sessions yet...",
                fontFamily = LATO,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
                lineHeight = 40.sp,
            )
        }

        Spacer(modifier = Modifier.weight(0.15f))
    }
}


@Composable
private fun CompletedSessionList(
    uiState: UserStatsUiState.Retrieved,
    onTaskClick: (Long) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        item {
            Spacer(Modifier.height(5.dp))
        }

        items(
            items = uiState.completedTasks,
            key = { it.taskId}
        ) { session ->
            CompletedTaskUIListItem(
                task = session,
                Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .height(100.dp)
                    .clickable(onClick = { onTaskClick(session.taskId) })
            )
        }
    }
}


@Preview
@Composable
private fun UserStatsPagePreview() {
    ClockworkTheme {
        UserStatsPage(
            uiState = UserStatsUiState.Retrieved(
                completedTasks = DummyData.SESSIONS
                    .filter { it is CompletedTask }
                    .map { (it as CompletedTask).toCompletedSessionListItem() }
            ),
            navBar = { NavBar(
                items = FAKE_TOP_LEVEL_DESTINATIONS,
                currentDestination = Unit::class,
                navigateTo = {},
            ) },
            onCompletedSessionClick = { },
        )
    }
}


@Preview
@Composable
private fun UserStatsNoCompletedSessionsPagePreview() {
    ClockworkTheme {
        UserStatsPage(
            uiState = UserStatsUiState.Retrieved(
                completedTasks = emptyList()
            ),
            navBar = {
                NavBar(
                    items = FAKE_TOP_LEVEL_DESTINATIONS,
                    currentDestination = Unit::class,
                    navigateTo = {},
                )
            },
            onCompletedSessionClick = { },
        )
    }
}