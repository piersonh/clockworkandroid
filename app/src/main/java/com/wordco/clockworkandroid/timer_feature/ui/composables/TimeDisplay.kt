package com.wordco.clockworkandroid.timer_feature.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
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
import com.wordco.clockworkandroid.timer_feature.ui.util.toHours
import com.wordco.clockworkandroid.timer_feature.ui.util.toMinutesInHour
import java.util.Locale


@Composable
fun TimeDisplay(
    uiState: TimerUiState.Retrieved
) {
    Box(
        Modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // TODO: Revisit this it seems ehh
            val statusText = when (uiState) {
                is TimerUiState.Paused -> "Taking Break"
                is TimerUiState.Shelved if uiState.isPreparing -> "Preparing..."
                is TimerUiState.Suspended -> "Suspended"
                else -> null
            }

            if (statusText != null) {
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
        }

        BasicText(
            text = uiState.elapsedSeconds.let { secs ->
                String.format(
                    Locale.getDefault(),
                    when (uiState) {
                        is TimerUiState.Running if secs % 2 == 1 -> "%02d %02d"
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
                fontFamily = ROBOTO,
                textAlign = TextAlign.Center,
            ),
            softWrap = false,
            maxLines = 1,
        )
//            Text(
//                text = uiState.elapsedSeconds.let {
//                    secs ->
//                    String.format(
//                        Locale.getDefault(),
//                        when (uiState) {
//                            is TimerUiState.Running if secs % 2 == 1 -> "%02d %02d"
//                            else -> "%02d:%02d"
//                        },
//                        secs.toHours(), secs.toMinutesInHour()
//                    )
//                },
//                textAlign = TextAlign.Center,
//                fontFamily = ROBOTO,
//                color = MaterialTheme.colorScheme.onPrimaryContainer,
//                softWrap = false,
//                modifier = Modifier.fillMaxWidth(),
//                onTextLayout = { result ->
//                    if (result.didOverflowWidth) {
//                        textStyle = textStyle.run{
//                            copy(fontSize = fontSize * 0.95)
//                        }
//                    }
//                }
//            )
    }
}

