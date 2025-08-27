package com.wordco.clockworkandroid.timer_feature.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.core.ui.composables.MarkImage
import com.wordco.clockworkandroid.core.ui.composables.MoonImage
import com.wordco.clockworkandroid.core.ui.composables.MugImage
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.timer_feature.ui.TimerUiState


@Composable
fun TimerControls(
    modifier: Modifier = Modifier,
    uiState: TimerUiState.Retrieved,
    onInitClick: () -> Unit,
    onBreakClick: () -> Unit,
    onSuspendClick: () -> Unit,
    onResumeClick: () -> Unit,
    onMarkClick: () -> Unit,
    onFinishClick: () -> Unit,
) {
    when (uiState) {
        is TimerUiState.New -> InitControls (
            modifier,
            onInitClick
        )
        is TimerUiState.Running -> RunningControls(
            modifier,
            onBreakClick,
            onSuspendClick,
            onMarkClick,
            onFinishClick
        )
        is TimerUiState.Paused -> PausedControls(
            modifier,
            onResumeClick,
            onFinishClick
        )
        is TimerUiState.Suspended -> SuspendedControls(
            modifier,
            onInitClick,
            onFinishClick
        )
    }
}


@Composable
fun InitControls(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    )
    {
        RectangleButton(
            onClick,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Text("Start", style = TextStyle(fontSize = 48.sp), fontFamily = LATO)
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun RunningControls(
    modifier: Modifier = Modifier,
    onPauseClick: () -> Unit,
    onSuspendClick: () -> Unit,
    onMarkClick: () -> Unit,
    onFinishClick: () -> Unit,
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pause
            CircleButton(onPauseClick, modifier = Modifier.fillMaxHeight()) {
                MugImage()
            }

            // Suspend
            CircleButton(onSuspendClick, modifier = Modifier.fillMaxHeight()) {
                MoonImage()
            }

            // Mark
            CircleButton(onMarkClick, modifier = Modifier.fillMaxHeight()) {
                MarkImage()
            }
        }

        RectangleButton(
            onFinishClick,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Text("Finish", style = TextStyle(fontSize = 48.sp), fontFamily = LATO)
        }
    }
}

@Composable
fun PausedControls(
    modifier: Modifier = Modifier,
    onResumeClick: () -> Unit,
    onFinishClick: () -> Unit
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    )
    {
        RectangleButton(
            onResumeClick,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Text("Resume", style = TextStyle(fontSize = 48.sp), fontFamily = LATO)
        }

        RectangleButton(
            onFinishClick,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Text("Finish", style = TextStyle(fontSize = 48.sp), fontFamily = LATO)
        }
    }
}

@Composable
fun SuspendedControls(
    modifier: Modifier = Modifier,
    onResumeClick: () -> Unit,
    onFinishClick: () -> Unit
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    )
    {
        RectangleButton(
            onResumeClick,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Text("Resume", style = TextStyle(fontSize = 48.sp), fontFamily = LATO)
        }

        RectangleButton(
            onFinishClick,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Text("Finish", style = TextStyle(fontSize = 48.sp), fontFamily = LATO)
        }
    }
}

//
//
//@Preview
//@Composable
//private fun NonterminatingControlsPreview() = NonterminatingControls(Timer())