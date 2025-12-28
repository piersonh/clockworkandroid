package com.wordco.clockworkandroid.timer_feature.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.core.ui.theme.ClockWorkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.timer_feature.ui.TimerUiState
import com.wordco.clockworkandroid.timer_feature.ui.util.toHours
import com.wordco.clockworkandroid.timer_feature.ui.util.toMinutesInHour
import java.util.Locale

data class StatusColors(
    val background: Color,
    val border: Color,
    val text: Color,
)

@Composable
fun TimeDisplay(
    uiState: TimerUiState.Retrieved
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TODO: Revisit this it seems ehh
        val statusText = when (uiState) {
            is TimerUiState.Running -> uiState.currentSegmentElapsedSeconds.let {
                String.format(
                    Locale.getDefault(),
                    "Time Working — %02d:%02d",
                    it.toHours(), it.toMinutesInHour()
                )
            }
            is TimerUiState.Paused -> uiState.currentSegmentElapsedSeconds.let {
                String.format(
                    Locale.getDefault(),
                    "Time On Break — %02d:%02d",
                    it.toHours(), it.toMinutesInHour()
                )
            }
            is TimerUiState.Shelved if uiState.isPreparing -> "Preparing..."
            is TimerUiState.Suspended -> "Task Suspended"
            is TimerUiState.Finished -> "Task Finished!"
            is TimerUiState.New -> "Ready to Start"
        }

        val statusColors = when (uiState) {
            is TimerUiState.Running -> StatusColors(
                background = MaterialTheme.colorScheme.tertiaryContainer,
                border = MaterialTheme.colorScheme.tertiary,
                text = Color(0xFF047857),
            )
            is TimerUiState.Paused -> StatusColors(
                background = Color(0xFFFEF3C7),
                border = Color(0xFFFCD34D),
                text = Color(0xFF92400E),
            )
            is TimerUiState.Suspended -> StatusColors(
                background = Color(0xFFFEE2E2),
                border = Color(0xFFFCA5A5),
                text = Color(0xFF991B1B),
            )
            else -> StatusColors(
                background = Color(0xFF8EC1FF),
                border = Color(0xFF3286FF),
                text = Color(0xFF004AA4),
            )
        }

        // Status
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = statusColors.background
            ),
            border = BorderStroke(1.dp, statusColors.border)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = statusColors.text,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Timer Display
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-8).dp), // the font has a lot of empty space above
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
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
                        maxFontSize = 112.sp
                    ),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontFamily = LATO,
                        fontSize = 112.sp,
                        fontWeight = FontWeight.Bold,
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

@Preview
@Composable
private fun TimeDisplayPreviewRunning() {
    ClockWorkTheme {
        TimeDisplay(
            uiState = TimerUiState.Running(
                taskName = "Preview",
                totalElapsedSeconds = 12345,
                currentSegmentElapsedSeconds = 119
            )
        )
    }
}

@Preview
@Composable
private fun TimeDisplayPreviewBreak() {
    ClockWorkTheme {
        TimeDisplay(
            uiState = TimerUiState.Paused(
                taskName = "Preview",
                totalElapsedSeconds = 12345,
                currentSegmentElapsedSeconds = 12345
            )
        )
    }
}

@Preview
@Composable
private fun TimeDisplayPreviewDone() {
    ClockWorkTheme {
        TimeDisplay(
            uiState = TimerUiState.Finished(
                taskName = "Preview",
                totalElapsedSeconds = 12345,
                isPreparing = false
            )
        )
    }
}

