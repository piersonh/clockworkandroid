package com.wordco.clockworkandroid.ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.domain.model.Timer
import com.wordco.clockworkandroid.ui.theme.LATO
import java.util.Locale

@Composable
fun TimeDisplay(timer: Timer, modifier: Modifier = Modifier) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Bottom
) {
    val elapsedTime by timer.secondsElapsed.collectAsState()
    val state by timer.state.collectAsState()
    val statusText = when (state) {
        Timer.State.PAUSED -> "Taking Break"
        Timer.State.SUSPENDED -> "Suspended"
        else -> null
    }

    if (statusText != null) {
        Text(
            text = statusText,
            style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center),
            fontFamily = LATO,
        )
    } else {
        Text (
            text = "",
            style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
        )
    }


    Text(
        text = String.format(
            Locale.getDefault(),
            if (elapsedTime % 2 == 1 && state == Timer.State.RUNNING) "%02d %02d" else "%02d:%02d",
            timer.getHours(), timer.getMinutesInHour()
        ),
        style = TextStyle(fontSize = 120.sp, textAlign = TextAlign.Center),
        fontFamily = LATO,
    )
}


@Preview
@Composable
private fun TimeDisplayPreview() = TimeDisplay(Timer())