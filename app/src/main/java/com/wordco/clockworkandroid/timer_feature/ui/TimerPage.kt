package com.wordco.clockworkandroid.timer_feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.timer_feature.ui.composables.TimeDisplay
import com.wordco.clockworkandroid.timer_feature.ui.composables.TimerControls


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPage(
    // See https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#jetpack-compose
    timerViewModel: TimerViewModel,// = viewModel(factory = TimerViewModel.Factory),
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val uiState by timerViewModel.uiState.collectAsStateWithLifecycle()

    TimerPage(
        uiState = uiState,
        onBackClick = onBackClick,
        onEditClick = onEditClick,
        onInitClick = timerViewModel::initTimer,
        onBreakClick = timerViewModel::takeBreak,
        onSuspendClick = timerViewModel::suspendTimer,
        onResumeClick = timerViewModel::resumeTimer,
        onMarkClick = timerViewModel::addMark,
        onFinishClick = timerViewModel::finish,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimerPage(
    uiState: TimerUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onInitClick: () -> Unit,
    onBreakClick: () -> Unit,
    onSuspendClick: () -> Unit,
    onResumeClick: () -> Unit,
    onMarkClick: () -> Unit,
    onFinishClick: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                title = {
                    Row(modifier = Modifier.padding(end = 10.dp)) {
                        IconButton(onClick = onBackClick) {
                            BackImage()
                        }

                        Spacer(Modifier.weight(1f))
                        if (uiState is TimerUiState.Shelved) {
                            Text(
                                modifier = Modifier.align(alignment = Alignment.CenterVertically)
                                    .clickable(onClick = onEditClick),
                                text = "Edit",
                                style = TextStyle(fontSize = 25.sp),
                                textAlign = TextAlign.Right,
                                fontFamily = LATO,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.TopEnd
        ) {

            when (uiState) {
                is TimerUiState.Retrieved -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.primaryContainer)
                            .padding(top = 50.dp)
                    ) {
                        Text(
                            text = uiState.taskName,
                            style = TextStyle(fontSize = 48.sp),
                            modifier = Modifier,
                            fontFamily = LATO,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        //Text(text = "${uiState.elapsedSeconds}")
                        TimeDisplay(uiState)


                        // TIMER CONTROLS
                        TimerControls(
                            modifier = Modifier
                                .aspectRatio(2f)
                                .padding(10.dp)
                                .defaultMinSize(minHeight = 200.dp),
                            uiState,
                            onInitClick,
                            onBreakClick,
                            onSuspendClick,
                            onResumeClick,
                            onMarkClick,
                            onFinishClick,
                        )
                    }
                }

                TimerUiState.Retrieving -> Text("Loading task...")
            }
        }
    }
}

@Preview
@Composable
private fun SuspendedTimerPagePreview() {
    ClockworkTheme {
        TimerPage(
            uiState = TimerUiState.Suspended(
                taskName = "Ooga Booga",
                elapsedSeconds = 1000
            ),
            onBackClick = {},
            onEditClick = {},
            onInitClick = {},
            onBreakClick = {},
            onSuspendClick = {},
            onResumeClick = {},
            onMarkClick = {},
            onFinishClick = {}
        )
    }
}