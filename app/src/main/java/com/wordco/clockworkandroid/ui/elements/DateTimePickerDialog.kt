package com.wordco.clockworkandroid.ui.elements

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog (
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    datePickerState: DatePickerState,
    timePickerState: TimePickerState
) {

    var state by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss
    ) {

        Card {
            Column {
                when (state) {
                    0 -> {
                        DatePicker(
                            state = datePickerState,
                            colors = DatePickerDefaults.colors(
                                selectedDayContainerColor = MaterialTheme.colorScheme.secondary,
                                todayContentColor = MaterialTheme.colorScheme.secondary,
                                todayDateBorderColor = MaterialTheme.colorScheme.secondary,
                                selectedDayContentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                    }

                    1 -> {
                        TimePicker(
                            state = timePickerState
                        )
                    }

                    else -> {
                        Log.println(
                            Log.WARN,
                            "DateTimePickerDialog",
                            "Improper state in dialog: $state"
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(
                            "Cancel",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    when (state) {
                        0 -> {
                            TextButton(
                                onClick = {
                                    state++
                                }
                            ) {
                                Text(
                                    "Next",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        1 -> {
                            TextButton(
                                onClick = {
                                    state--
                                }
                            ) {
                                Text(
                                    "Back",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }


                            TextButton(
                                onClick = onConfirm
                            ) {
                                Text(
                                    "Ok",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}