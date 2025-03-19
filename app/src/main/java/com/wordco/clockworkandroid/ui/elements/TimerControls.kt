package com.wordco.clockworkandroid.ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.model.Timer


@Composable()
fun TimerControls(
    timer: Timer,
    modifier: Modifier = Modifier,
    horizontalSpacingRatio: Float = 0.4f,
    verticalSpacingRatio: Float = 0.2f,
) = Column(
    modifier = modifier.aspectRatio((3 + 2 * horizontalSpacingRatio)/(2 + verticalSpacingRatio)),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {

    @Composable
    fun InitControls() {
        Spacer(modifier = Modifier.size(80.dp).weight(0.5f + verticalSpacingRatio/2f))
        RectangleButton({ timer.startTimer() }, modifier = Modifier.fillMaxSize().weight(1f)) {
            Text("Start", style = TextStyle(fontSize = 48.sp))
        }
        Spacer(modifier = Modifier.size(80.dp).weight(0.5f + verticalSpacingRatio/2f))
    }

    @Composable
    fun RunningControls() {
        NonterminatingControls(timer, modifier = Modifier.weight(1f), spacingToButtonWidth = horizontalSpacingRatio)

        Spacer(modifier = Modifier.height(80.dp).weight(verticalSpacingRatio))

        RectangleButton({ timer.stopTimer() }, modifier = Modifier.fillMaxSize().weight(1f)) {
            Text("Finish", style = TextStyle(fontSize = 48.sp))
        }
    }

    @Composable
    fun PausedControls() {
        RectangleButton({ timer.startTimer() }, modifier = Modifier.fillMaxSize().weight(1f)) {
            Text("Resume", style = TextStyle(fontSize = 48.sp))
        }

        Spacer(modifier = Modifier.height(80.dp).weight(verticalSpacingRatio))

        RectangleButton({ timer.stopTimer() }, modifier = Modifier.fillMaxSize().weight(1f)) {
            Text("Finish", style = TextStyle(fontSize = 48.sp))
        }
    }

    val state by timer.state.collectAsState()

    when (state) {
        Timer.State.INIT -> InitControls()
        Timer.State.RUNNING -> RunningControls()
        Timer.State.PAUSED,Timer.State.SUSPENDED -> PausedControls()
        else -> throw RuntimeException("Additional State Controls Not Implemented")
    }
}



@Preview()
@Composable
private fun ControlButtonsPreview() = TimerControls(Timer(Timer.State.PAUSED))

@Composable
fun NonterminatingControls(timer: Timer, modifier: Modifier = Modifier, spacingToButtonWidth: Float = 0.5f) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Pause
        CircleButton({ timer.stopTimer() }, modifier = Modifier.width(80.dp).weight(1f)) {
            StarImage()
        }

        Spacer(modifier = Modifier.size(80.dp).weight(spacingToButtonWidth))

        // Suspend
        CircleButton({}, modifier = Modifier.width(40.dp).weight(1f)) {
            StarImage()
        }

        Spacer(modifier = Modifier.width(80.dp).weight(spacingToButtonWidth))


        // Mark
        CircleButton({}, modifier = Modifier.width(80.dp).weight(1f)) {
            StarImage()
        }
    }
}


@Preview
@Composable
private fun NonterminatingControlsPreview() = NonterminatingControls(Timer())