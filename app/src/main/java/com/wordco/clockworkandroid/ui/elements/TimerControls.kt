package com.wordco.clockworkandroid.ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.wordco.clockworkandroid.domain.Timer
import com.wordco.clockworkandroid.ui.theme.LATO


@Composable
fun TimerControls(
    timer: Timer,
    modifier: Modifier = Modifier,
    navController: NavController
) = Column(
    modifier = modifier.aspectRatio(2f),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(20.dp),
) {
    @Composable
    fun InitControls() {
        RectangleButton({ timer.startTimer() }, modifier = Modifier.fillMaxSize().weight(1f)) {
            Text("Start", style = TextStyle(fontSize = 48.sp), fontFamily = LATO)
        }
        Spacer(modifier = Modifier.weight(1f))
    }

    @Composable
    fun RunningControls() {
        NonterminatingControls(timer, modifier = Modifier.weight(1f))

        RectangleButton({
                            timer.stopTimer()
                            navController.navigate("TaskCompletionPage")
                        }, modifier = Modifier.fillMaxSize().weight(1f)) {
            Text("Finish", style = TextStyle(fontSize = 48.sp), fontFamily = LATO)
        }
    }

    @Composable
    fun PausedControls() {
        RectangleButton({ timer.startTimer() }, modifier = Modifier.fillMaxSize().weight(1f)) {
            Text("Resume", style = TextStyle(fontSize = 48.sp), fontFamily = LATO)
        }



        RectangleButton({
                            timer.stopTimer()
                            navController.navigate("TaskCompletionPage")
                        }, modifier = Modifier.fillMaxSize().weight(1f)) {
            Text("Finish", style = TextStyle(fontSize = 48.sp), fontFamily = LATO)
        }
    }

    val state by timer.state.collectAsState()

    when (state) {
        Timer.State.INIT -> InitControls()
        Timer.State.RUNNING -> RunningControls()
        Timer.State.PAUSED, Timer.State.SUSPENDED -> PausedControls()
        else -> throw RuntimeException("Additional State Controls Not Implemented")
    }
}



@Preview
@Composable
private fun ControlButtonsPreviewPaused() {
    val navController = rememberNavController()
    TimerControls(Timer(Timer.State.PAUSED), navController = navController)
}

@Preview
@Composable
private fun ControlButtonsPreviewRunning() {
    val navController = rememberNavController()
    TimerControls(Timer(Timer.State.RUNNING), navController = navController)
}

@Preview
@Composable
private fun ControlButtonsPreviewInit() {
    val navController = rememberNavController()
    TimerControls(Timer(Timer.State.INIT), navController = navController)
}


@Composable
fun NonterminatingControls(timer: Timer, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pause
        CircleButton({ timer.stopTimer() }, modifier = Modifier.fillMaxHeight()) {
            MugImage()
        }

        // Suspend
        CircleButton({}, modifier = Modifier.fillMaxHeight()) {
            MoonImage()
        }

        // Mark
        CircleButton({}, modifier = Modifier.fillMaxHeight()) {
            MarkImage()
        }
    }
}


@Preview
@Composable
private fun NonterminatingControlsPreview() = NonterminatingControls(Timer())