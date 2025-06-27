package com.wordco.clockworkandroid.ui.elements

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
import com.wordco.clockworkandroid.ui.TimerState
import com.wordco.clockworkandroid.ui.theme.LATO


@Composable
fun TimerControls(
    modifier: Modifier = Modifier,
    timerState: TimerState?,
    onStartClick: () -> Unit,
    onBreakClick: () -> Unit,
    onSuspendClick: () -> Unit,
    onResumeClick: () -> Unit,
    onMarkClick: () -> Unit,
    onFinishClick: () -> Unit,
) {
    when (timerState) {
        TimerState.WAITING -> InitControls (modifier, onStartClick)
        TimerState.RUNNING -> RunningControls(
            modifier,
            onBreakClick,
            onSuspendClick,
            onMarkClick,
            onFinishClick
        )

        TimerState.BREAK, TimerState.SUSPENDED -> PausedControls(
            modifier,
            onResumeClick,
            onFinishClick
        )

        else -> throw RuntimeException("Additional State Controls Not Implemented")
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

//
//
//@Preview
//@Composable
//private fun NonterminatingControlsPreview() = NonterminatingControls(Timer())