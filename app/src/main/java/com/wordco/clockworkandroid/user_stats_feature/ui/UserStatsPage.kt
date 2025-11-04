package com.wordco.clockworkandroid.user_stats_feature.ui

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.composables.NavBar
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews
import com.wordco.clockworkandroid.core.ui.util.FAKE_TOP_LEVEL_DESTINATIONS
import com.wordco.clockworkandroid.session_completion_feature.domain.use_case.CalculateEstimateAccuracyUseCase
import com.wordco.clockworkandroid.user_stats_feature.ui.composables.CompletedTaskUIListItem
import com.wordco.clockworkandroid.user_stats_feature.ui.model.ExportDataError
import com.wordco.clockworkandroid.user_stats_feature.ui.model.mapper.toCompletedSessionListItem
import com.wordco.clockworkandroid.user_stats_feature.ui.util.Result
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

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
        onExportUserData = viewModel::onExportUserData,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserStatsPage(
    uiState: UserStatsUiState,
    navBar: @Composable () -> Unit,
    onCompletedSessionClick: (Long) -> Unit,
    onExportUserData: suspend () -> Result<String, ExportDataError>,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "User History",
                        fontFamily = LATO,
                        fontWeight = FontWeight.Black,
                    )
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                actions = {
                    TextButton(
                        onClick = {
                            scope.launch (Dispatchers.IO) {
                                val result = onExportUserData()
                                val message = when (result) {
                                    is Result.Error<ExportDataError> -> {
                                        when (result.error) {
                                            ExportDataError.NO_URI -> "Export Failed: No URI"
                                            ExportDataError.NO_PERMISSION -> "Export Failed: No Permission"
                                            ExportDataError.IO -> "Export Failed: IO Error"
                                            ExportDataError.NO_FILE -> "Export Failed: File not found"
                                            ExportDataError.SECURITY -> "Export Failed: Security error"
                                            ExportDataError.OTHER -> "Export Failed: Error"
                                        }
                                    }
                                    is Result.Success<String> -> "Export Complete: downloads/${result.result}"
                                }
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    ) {
                        Text(
                            text = "Export",
                            style = TextStyle(fontSize = 25.sp),
                            textAlign = TextAlign.Right,
                            fontFamily = LATO,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
            )
        },
        bottomBar = navBar,
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.primary)
        )
        {
            when (uiState) {
                is UserStatsUiState.Retrieved if uiState.completedTasks.isEmpty() -> EmptyTaskList()
                is UserStatsUiState.Retrieved ->
                    Column (
                        modifier = Modifier
                    ) {

                        LineChart(
                            modifier = Modifier
                                .fillMaxHeight(0.3f)
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp, vertical = 10.dp),
                            data = remember(uiState.accuracyChartData) {
                                listOf(
                                    Line(
                                        label = "Completed tasks accuracy",
                                        values = uiState.accuracyChartData,
                                        color = SolidColor(Color(0xFF23af92)),
                                        firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                                        secondGradientFillColor = Color.Transparent,
                                        strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                                        gradientAnimationDelay = 1000,
                                        drawStyle = DrawStyle.Stroke(width = 2.dp),
                                        dotProperties = DotProperties(
                                            enabled = true,
                                            color = SolidColor(Color(0xFF23af92))
                                        ),
                                        popupProperties = PopupProperties(
                                            textStyle = TextStyle(
                                                color = Color.White
                                            ),
                                            contentBuilder = {
                                                String.format(
                                                    Locale.getDefault(),
                                                    "%.0f%%",
                                                    it.value
                                                )
                                            }
                                        )
                                    )
                                )
                            },
                            animationMode = AnimationMode.Together(delayBuilder = {
                                it * 500L
                            }),
                            minValue = 0.0,
                            maxValue = 100.0,
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CompletedSessionList(
                            uiState,
                            onTaskClick = onCompletedSessionClick,
                        )
                    }
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
                contentDescription = "Trophy",
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
                color = MaterialTheme.colorScheme.onPrimaryContainer,
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
            key = { it.taskId }
        ) { session ->
            CompletedTaskUIListItem(
                task = session,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = { onTaskClick(session.taskId) },
                )
            }
        }
    }



@AspectRatioPreviews
@Composable
private fun UserStatsPagePreview() {
    val calculateEstimateAccuracyUseCase = remember { CalculateEstimateAccuracyUseCase() }

    ClockworkTheme {
        UserStatsPage(
            uiState = UserStatsUiState.Retrieved(
                completedTasks = DummyData.SESSIONS
                    .filterIsInstance<CompletedTask>()
                    .map { it.toCompletedSessionListItem() },
                accuracyChartData = DummyData.SESSIONS
                    .filterIsInstance<CompletedTask>()
                    .mapNotNull { 
                        it.userEstimate?.let { userEstimate ->
                            calculateEstimateAccuracyUseCase(it.workTime.plus(it.breakTime),
                                userEstimate)
                        } 
                    }
            ),
            navBar = {
                NavBar(
                    items = FAKE_TOP_LEVEL_DESTINATIONS,
                    currentDestination = Unit::class,
                    navigateTo = {},
                )
            },
            onCompletedSessionClick = {  },
            onExportUserData = { Result.Success("") },
        )
    }
}


@AspectRatioPreviews
@Composable
private fun UserStatsNoCompletedSessionsPagePreview() {
    ClockworkTheme {
        UserStatsPage(
            uiState = UserStatsUiState.Retrieved(
                completedTasks = emptyList(),
                accuracyChartData = emptyList(),
            ),
            navBar = {
                NavBar(
                    items = FAKE_TOP_LEVEL_DESTINATIONS,
                    currentDestination = Unit::class,
                    navigateTo = {},
                )
            },
            onCompletedSessionClick = {  },
            onExportUserData = { Result.Success("") },
        )
    }
}
