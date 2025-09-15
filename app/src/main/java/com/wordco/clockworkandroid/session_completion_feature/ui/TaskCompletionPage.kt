package com.wordco.clockworkandroid.session_completion_feature.ui

// import androidx.compose.ui.graphics.BlendMode.Companion.Color // This import seems unused and conflicts with androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.core.domain.model.Marker
import com.wordco.clockworkandroid.core.domain.model.Segment
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.Instant
import java.time.LocalDate


@Composable
fun TaskCompletionPage(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    taskCompletionViewModel: TaskCompletionViewModel
) {
    val uiState = taskCompletionViewModel.uiState.collectAsStateWithLifecycle()

    //FIXME
    TaskCompletionPage(
        uiState = uiState as TaskCompletionUiState,
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
                        // FIXME
                        text = uiState.name,
                        style = TextStyle(fontSize = 40.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.weight(0.004f))

                    Text(
                        text = "Completed!",
                        style = TextStyle(fontSize = 26.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.weight(0.03f))

                    Text(
                        // FIXME
                        text = "Total Time: 00:00",//taskViewModel.currentTask!!.workTime.asHHMM(),
                        style = TextStyle(fontSize = 90.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.weight(0.03f))

                    Text(
                        text = "You estimated",
                        style = TextStyle(fontSize = 26.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Text(
                        // TODO: app estimation
                        text = "TODO",
                        style = TextStyle(fontSize = 34.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.weight(0.03f))

                    Text(
                        text = "--% overestimate",
                        style = TextStyle(fontSize = 26.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.weight(0.01f))

                    Text(
                        text = "--% improvement from recent averages",
                        style = TextStyle(fontSize = 26.sp), maxLines = 2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
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

@Preview
@Composable
private fun TaskCompletionPagePreview() {
    val previewTaskId = 1L
    val now = Instant.now()
    ClockworkTheme {
        TaskCompletionPage(
            uiState = TaskCompletionUiState.Retrieved(
                name = "Preview Task",
                dueDate = LocalDate.now(),
                difficulty = 3f,
                color = Color.Red,
                estimate = UserEstimate(60, 1),
                markers = listOf(
                    Marker(
                        markerId = 1L,
                        taskId = previewTaskId,
                        startTime = now.minusSeconds(1800),
                        label = "Halfway point"),
                    Marker(
                        markerId = 2L,
                        taskId = previewTaskId,
                        startTime = now.minusSeconds(600),
                        label = "Quick check-in")
                ),
                segments = listOf(
                    Segment(
                        segmentId = 1L,
                        taskId = previewTaskId,
                        startTime = now.minusSeconds(3600),
                        null,
                        Segment.Type.WORK
                    ),
                    Segment(
                        segmentId = 2L,
                        taskId = previewTaskId,
                        startTime = now.minusSeconds(1800),
                        duration = null,
                        type = Segment.Type.BREAK
                    ),
                    Segment(
                        segmentId = 3L,
                        taskId = previewTaskId,
                        startTime = now.minusSeconds(600),
                        duration = null,
                        type = Segment.Type.FINISH
                    )
                )
            ),
            onBackClick = {},
            onContinueClick = {}
        )
    }
}
