package com.wordco.clockworkandroid.timer_feature.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.core.ui.theme.ClockWorkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.theme.TIME_DISPLAY
import com.wordco.clockworkandroid.timer_feature.ui.TimerUiState
import com.wordco.clockworkandroid.timer_feature.ui.util.toHours
import com.wordco.clockworkandroid.timer_feature.ui.util.toMinutesInHour
import java.util.Locale


@Composable
fun TimeDisplay(
    uiState: TimerUiState.Retrieved
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 20.dp, vertical = 5.dp),
        ) {
            // TODO: Revisit this it seems ehh
            val statusText = when (uiState) {
                is TimerUiState.Running -> uiState.currentSegmentElapsedSeconds.let {
                    String.format(
                        Locale.getDefault(),
                        "Working — %02d:%02d",
                        it.toHours(), it.toMinutesInHour()
                    )
                }
                is TimerUiState.Paused -> uiState.currentSegmentElapsedSeconds.let {
                    String.format(
                        Locale.getDefault(),
                        "On Break — %02d:%02d",
                        it.toHours(), it.toMinutesInHour()
                    )
                }
                is TimerUiState.Shelved if uiState.isPreparing -> "Preparing..."
                is TimerUiState.Suspended -> "Suspended"
                is TimerUiState.Finished -> "Finished!"
                is TimerUiState.New -> "Ready to Start"
            }

            Text(
                text = statusText,
                style = TextStyle(
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center
                ),
                fontFamily = LATO,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                modifier = Modifier.offset(y = (-8).dp) // the font has a lot of empty space above
                    .fillMaxWidth(),
                text = uiState.totalElapsedSeconds.let { secs ->
                    String.format(
                        Locale.getDefault(),
                        when (uiState) {
                            is TimerUiState.Active if secs % 2 == 1 -> "%02d %02d"
                            else -> "%02d:%02d"
                        },
                        secs.toHours(), secs.toMinutesInHour()
                    )
                },
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 50.sp,
                    maxFontSize = 120.sp
                ),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontFamily = TIME_DISPLAY,
                    textAlign = TextAlign.Center,
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                softWrap = false,
                maxLines = 1,
            )

        }
    }
}


@Preview
@Composable
private fun TimerDisplayPreview() {
    ClockWorkTheme {
        TimeDisplay(
            uiState = TimerUiState.Suspended(
                taskName = "Preview",
                totalElapsedSeconds = 12345,
                isPreparing = false
            )
        )
    }
}

