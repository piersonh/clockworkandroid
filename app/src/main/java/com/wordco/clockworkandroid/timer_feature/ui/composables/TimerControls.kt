package com.wordco.clockworkandroid.timer_feature.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.ui.theme.ClockWorkTheme
import com.wordco.clockworkandroid.timer_feature.ui.TimerUiState


@Composable
fun TimerControls(
    modifier: Modifier = Modifier,
    uiState: TimerUiState.Retrieved,
    onInitClick: () -> Unit,
    onBreakClick: () -> Unit,
    onSuspendClick: () -> Unit,
    onResumeClick: () -> Unit,
    onMarkClick: () -> Unit,
    onFinishClick: () -> Unit,
) {
    when (uiState) {
        is TimerUiState.New -> InitControls (
            modifier,
            onInitClick,
            uiState.isPreparing
        )
        is TimerUiState.Running -> RunningControls(
            modifier,
            onBreakClick,
            onSuspendClick,
            onMarkClick,
            onFinishClick
        )
        is TimerUiState.Paused -> PausedControls(
            modifier,
            onResumeClick,
            onFinishClick
        )
        is TimerUiState.Suspended -> SuspendedControls(
            modifier,
            onInitClick,
            onFinishClick,
            uiState.isPreparing
        )
        is TimerUiState.Finished -> {}
    }
}


@Composable
fun InitControls(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isPreparing: Boolean,
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        // Start Button
        Button(
            onClick = if (isPreparing) {{}} else onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.start),
                contentDescription = if (isPreparing) "Preparing..." else "Start",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSecondary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (isPreparing) "Preparing..."  else "Start",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
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
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            // Pause
            ActionButton(
                modifier = Modifier.weight(1f),
                onClick = onPauseClick,
                icon = R.drawable.mug,
                label = "Break",
                color = MaterialTheme.colorScheme.secondary,
            )

            // Suspend
            ActionButton(
                modifier = Modifier.weight(1f),
                onClick = onSuspendClick,
                icon = R.drawable.moon,
                label = "Suspend",
                color = MaterialTheme.colorScheme.secondary,
            )

            // Add Marker
            ActionButton(
                modifier = Modifier.weight(1f),
                onClick = onMarkClick,
                icon = R.drawable.bookmark,
                label = "Add Marker",
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        // Finish Button
        OutlinedButton(
            onClick = onFinishClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, Color.Gray.copy(alpha = 0.3f)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.stop),
                contentDescription = "Finish",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Finish",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
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
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        Button(
            onClick = onResumeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.start),
                contentDescription = "Resume",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSecondary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Resume",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        OutlinedButton(
            onClick = onFinishClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, Color.Gray.copy(alpha = 0.3f)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.stop),
                contentDescription = "Finish",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Finish",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SuspendedControls(
    modifier: Modifier = Modifier,
    onResumeClick: () -> Unit,
    onFinishClick: () -> Unit,
    isPreparing: Boolean,
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        Button(
            onClick = if (isPreparing) {{}} else onResumeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.start),
                contentDescription = if (isPreparing) "Preparing..." else "Resume",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSecondary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (isPreparing) "Preparing..."  else "Resume",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        OutlinedButton(
            onClick = onFinishClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, Color.Gray.copy(alpha = 0.3f)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.stop),
                contentDescription = "Finish",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Finish",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: Int,
    label: String,
    color: Color
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            ),
            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = label,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewTimerControlsNew() {
    ClockWorkTheme {
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        ) {
            TimerControls(
                uiState = TimerUiState.New(
                    taskName = "Task",
                    totalElapsedSeconds = 12345,
                    isPreparing = false
                ),
                onInitClick = {},
                onBreakClick = {},
                onSuspendClick = {},
                onResumeClick = {},
                onMarkClick = {},
                onFinishClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTimerControlsPaused() {
    ClockWorkTheme {
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        ) {
            TimerControls(
                uiState = TimerUiState.Paused(
                    taskName = "Task",
                    totalElapsedSeconds = 12345,
                    currentSegmentElapsedSeconds = 123
                ),
                onInitClick = {},
                onBreakClick = {},
                onSuspendClick = {},
                onResumeClick = {},
                onMarkClick = {},
                onFinishClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTimerControlsSuspended() {
    ClockWorkTheme {
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        ) {
            TimerControls(
                uiState = TimerUiState.Suspended(
                    taskName = "Task",
                    totalElapsedSeconds = 12345,
                    isPreparing = false
                ),
                onInitClick = {},
                onBreakClick = {},
                onSuspendClick = {},
                onResumeClick = {},
                onMarkClick = {},
                onFinishClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTimerControls() {
    ClockWorkTheme {
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        ) {
            TimerControls(
                uiState = TimerUiState.Running(
                    taskName = "Task",
                    totalElapsedSeconds = 12345,
                    currentSegmentElapsedSeconds = 123
                ),
                onInitClick = {},
                onBreakClick = {},
                onSuspendClick = {},
                onResumeClick = {},
                onMarkClick = {},
                onFinishClick = {}
            )
        }
    }
}