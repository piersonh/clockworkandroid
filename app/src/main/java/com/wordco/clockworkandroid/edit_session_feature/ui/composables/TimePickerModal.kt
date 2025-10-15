package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.wordco.clockworkandroid.core.ui.theme.LATO
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPickerModal(
    timePickerState: TimePickerState,
    onValueChange: (LocalTime) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Select Time",
                    style = MaterialTheme.typography.labelMedium
                )
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        selectorColor = MaterialTheme.colorScheme.secondaryContainer,
                        clockDialSelectedContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismissRequest) {
                        Text(
                            "Cancel",
                            fontFamily = LATO,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                    TextButton(onClick = {
                        val time = timePickerState.run {
                            LocalTime.of(hour, minute)
                        }
                        onValueChange(time)
                        onDismissRequest()
                    }
                    ) {
                        Text(
                            "OK",
                            fontFamily = LATO,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }
        }
    }
}