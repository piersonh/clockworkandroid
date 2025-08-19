package com.wordco.clockworkandroid.ui.elements

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier =
                Modifier
                    .width(360.dp)
                    //.height(IntrinsicSize.Min)
                    .background(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface
                    ),
        ) {
            Column (

            ) {
                when (state) {
                    0 -> {
                        DatePicker(
                            state = datePickerState,
                            colors = DatePickerDefaults.colors(
                                selectedDayContainerColor = MaterialTheme.colorScheme.secondary,
                                todayContentColor = MaterialTheme.colorScheme.secondary,
                                todayDateBorderColor = MaterialTheme.colorScheme.secondary,
                                selectedDayContentColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            //modifier = Modifier.requiredWidth(360.dp)
                            //    .scale(0.8f).align(Alignment.CenterHorizontally)

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
                            Spacer(
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = {
                                    state++
                                }
                            ) {
                                Text(
                                    "Next",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
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

                            Spacer(
                                modifier = Modifier.weight(1f)
                            )


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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Prev() = DateTimePickerDialog(
    onDismiss = { },
    onConfirm = { },
    datePickerState = rememberDatePickerState(),
    timePickerState = rememberTimePickerState()
)