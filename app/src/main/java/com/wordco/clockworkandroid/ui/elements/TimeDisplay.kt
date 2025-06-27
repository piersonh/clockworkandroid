package com.wordco.clockworkandroid.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.ui.TimerState
import com.wordco.clockworkandroid.ui.theme.LATO
import com.wordco.clockworkandroid.ui.theme.ROBOTO
import java.util.Locale


@Composable
fun TimeDisplay(
    timerState: TimerState?,
    secondsElapsed: Int?
) {
    // TIME DISPLAY
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 10.dp)
        ) {

            // TODO: Revisit this it seems ehh
            val statusText = when (timerState) {
                TimerState.BREAK -> "Taking Break"
                TimerState.SUSPENDED -> "Suspended"
                else -> null
            }

            if (statusText != null) {
                Text(
                    text = statusText,
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center),
                    fontFamily = LATO,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "",
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center)
                )
            }


            Text(
                text = String.format(
                    Locale.getDefault(),
                    if (secondsElapsed!! % 2 == 1 && timerState == TimerState.RUNNING) "%02d %02d" else "%02d:%02d",
                    secondsElapsed.toHours(), secondsElapsed.toMinutesInHour()
                ),
                style = TextStyle(fontSize = 120.sp, textAlign = TextAlign.Center),
                fontFamily = ROBOTO,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

fun Int.toHours(): Int {
    return this / 3600
}

fun Int.toMinutesInHour() : Int {
    return (this % 3600) / 60
}

//
//
//@Preview
//@Composable
//private fun TimeDisplayPreview() = TimeDisplay(Timer())