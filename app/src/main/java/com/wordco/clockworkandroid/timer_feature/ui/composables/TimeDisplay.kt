package com.wordco.clockworkandroid.timer_feature.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.theme.ROBOTO
import com.wordco.clockworkandroid.timer_feature.ui.TimerUiState
import java.util.Locale


@Composable
fun TimeDisplay(
    uiState: TimerUiState.Retrieved
) {
    // TIME DISPLAY
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(10.dp)
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
            val statusText = when (uiState) {
                is TimerUiState.Paused -> "Taking Break"
                is TimerUiState.Suspended -> "Suspended"
                else -> null
            }

            if (statusText != null) {
                Text(
                    text = statusText,
                    style = TextStyle(fontSize = 30.sp, textAlign = TextAlign.Center),
                    fontFamily = LATO,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
            }


            Text(
                text = uiState.elapsedSeconds.let {
                    secs ->
                    String.format(
                        Locale.getDefault(),
                        when (uiState) {
                            is TimerUiState.Running if secs % 2 == 1 -> "%02d %02d"
                            else -> "%02d:%02d"
                        },
                        secs.toHours(), secs.toMinutesInHour()
                    )
                },
                style = TextStyle(fontSize = 120.sp, textAlign = TextAlign.Center),
                fontFamily = ROBOTO,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth()
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