package com.wordco.clockworkandroid.session_completion_feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.Duration

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
        containerColor = MaterialTheme.colorScheme.primary, topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ), title = {
                    Row(modifier = Modifier.padding(end = 10.dp)) {
                        IconButton(onClick = onBackClick) {
                            BackImage()
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when (uiState) {
            TaskCompletionUiState.Retrieving -> Text("Retrieving...")
            is TaskCompletionUiState.Retrieved -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(color = MaterialTheme.colorScheme.primary),
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
                        "${uiState.estimate.hours}h ${uiState.estimate.minutes}m"
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



                    uiState.estimate?.let {
                        Spacer(modifier = Modifier.weight(0.03f))

                        val userTime = it.toDuration()
                        val taskTime = uiState.totalTime
                        val userAccuracy = calculateEstimateAccuracy(taskTime, userTime)
                        Text (
                            text = "Your accuracy: ${userAccuracy?.toInt()}%",
                            style = TextStyle(fontSize = 26.sp),
                            textAlign = TextAlign.Center,
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.03f))


                    Button(
                        onClick = { /* TODO: Handle View Details */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            text = "View Details",
                            style = TextStyle(fontSize = 24.sp),
                            fontFamily = LATO,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.03f))

                    Button(
                        onClick = onContinueClick,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            text = "Continue",
                            style = TextStyle(fontSize = 30.sp),
                            fontFamily = LATO,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.04f))
                }
            }
        }
    }
}


private fun calculateEstimateAccuracy(
    taskTime: Duration,
    userTime: Duration?
): Float? {
    if (userTime == null || userTime.isZero || taskTime.isZero) {
        return null
    }

    val actualSeconds = taskTime.seconds.toFloat()
    val estimatedSeconds = userTime.seconds.toFloat()

    return if (actualSeconds == 0f || estimatedSeconds == 0f) {
        null
    } else if (estimatedSeconds >= actualSeconds) {
        (actualSeconds / estimatedSeconds) * 100f
    } else {
        (estimatedSeconds / actualSeconds) * 100f
    }
}

@Preview
@Composable
private fun TaskCompletionPagePreview() {
    ClockworkTheme {
        TaskCompletionPage(
            uiState = TaskCompletionUiState.Retrieved(
                name = "Preview Task",
                estimate = UserEstimate(6, 1),
                workTime = Duration.ofSeconds(1800),
                breakTime = Duration.ofSeconds(1200),
                totalTime = Duration.ofSeconds(3600)
            ),
            onBackClick = {},
            onContinueClick = {}
        )
    }
}
