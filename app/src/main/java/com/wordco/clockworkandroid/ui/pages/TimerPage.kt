package com.wordco.clockworkandroid.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.model.Timer
import com.wordco.clockworkandroid.ui.elements.TimeDisplay
import com.wordco.clockworkandroid.ui.elements.TimerControls


@Composable
fun TimerPage(timer: Timer) {
    val state by timer.state.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(10.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(modifier = Modifier) {
                Text(text = "Back",style = TextStyle(fontSize = 30.sp))
                Spacer(Modifier.weight(1f))
                if (state == Timer.State.INIT) {
                    Text(
                        text = "Edit",
                        style = TextStyle(fontSize = 30.sp),
                        textAlign = TextAlign.Right
                    )
                }
            }

            Spacer(modifier = Modifier.weight(.5f))

            Text(
                text = "Task Name",
                style = TextStyle(fontSize = 48.sp),
                modifier = Modifier
            )

            Spacer(modifier = Modifier.weight(0.6f))

            TimeDisplay(timer, modifier = Modifier)

            Spacer(modifier = Modifier.weight(3f))

            TimerControls(timer, modifier = Modifier.padding(10.dp))
        }
    }
}


@Preview
@Composable
private fun TimerPagePreview() = TimerPage(Timer(Timer.State.INIT))
