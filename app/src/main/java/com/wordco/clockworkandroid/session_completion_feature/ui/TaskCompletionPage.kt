package com.wordco.clockworkandroid.session_completion_feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.composables.AccentRectangleTextButton
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import java.time.Duration
import kotlin.math.roundToInt

@Composable
fun TaskCompletionPage(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    taskCompletionViewModel: TaskCompletionViewModel
) {
    val uiState by taskCompletionViewModel.uiState.collectAsStateWithLifecycle()

    TaskCompletionPage(
        uiState = uiState,
        onBackClick = onBackClick,
        onContinueClick = onContinueClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskCompletionPage(
    uiState: TaskCompletionUiState,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Session Report",
                        fontFamily = LATO,
                        fontWeight = FontWeight.Black,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        BackImage()
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
            )
        },
    ) { innerPadding ->
        when (uiState) {
            TaskCompletionUiState.Retrieving -> Text("Retrieving...")
            is TaskCompletionUiState.Retrieved -> SessionReport(
                uiState = uiState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(color = MaterialTheme.colorScheme.primary),
                onContinueClick = onContinueClick
            )
        }
    }
}


@Composable
private fun SessionReport(
    uiState: TaskCompletionUiState.Retrieved,
    modifier: Modifier = Modifier,
    onContinueClick: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(all = 10.dp)
        ) {

            Text(
                text = uiState.name,
                style = TextStyle(fontSize = 40.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.weight(0.004f))

            Text(
                text = "${uiState.totalTime.toHours()}h " +
                        "${uiState.totalTime.toMinutes().mod(60)}m",
                style = TextStyle(fontSize = 90.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.weight(0.03f))

            val estimateText = if (uiState.estimate != null) {
                "${uiState.estimate.toHours()}h ${uiState.estimate.toMinutes().mod(60)}m"
            } else {
                "No estimate provided"
            }
            Text(
                text = "You estimated: $estimateText",
                style = TextStyle(fontSize = 26.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.weight(0.03f))

            Text(
                text = "Work time: ${uiState.workTime.toHours()}h " +
                        "${uiState.workTime.toMinutes().mod(60)}m",
                style = TextStyle(fontSize = 26.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.weight(0.03f))

            Text (
                text = "Break time: ${uiState.breakTime.toHours()}h " +
                        "${uiState.breakTime.toMinutes().mod(60)}m",
                style = TextStyle(fontSize = 26.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )



            uiState.totalTimeAccuracy?.let {
                Spacer(modifier = Modifier.weight(0.03f))
                Text (
                    text = "Your accuracy: ${uiState.totalTimeAccuracy.roundToInt()}%",
                    style = TextStyle(fontSize = 26.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.weight(0.03f))

            AccentRectangleTextButton(
                onClick = { /* TODO: Handle View Details */ },
                maxHeight = 58.dp,
            ) {
                Text(
                    text = "View Details",
                    fontFamily = LATO,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AccentRectangleTextButton(
                onClick = onContinueClick,
            ) {
                Text(
                    text = "Continue",
                    fontFamily = LATO,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                )
            }

            Spacer(modifier = Modifier.weight(0.04f))
        }
    }
}


@Preview
@Composable
private fun TaskCompletionPagePreview() {
    ClockworkTheme {
        TaskCompletionPage(
            uiState = TaskCompletionUiState.Retrieved(
                name = "Preview Task",
                estimate = Duration.ofMinutes(10).plusHours(1),
                workTime = Duration.ofMinutes(30),
                breakTime = Duration.ofMinutes(20),
                totalTime = Duration.ofMinutes(50),
                totalTimeAccuracy = 80.0,
            ),
            onBackClick = {},
            onContinueClick = {}
        )
    }
}
