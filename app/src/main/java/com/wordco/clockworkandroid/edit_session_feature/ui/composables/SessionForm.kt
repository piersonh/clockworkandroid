package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.core.ui.composables.ColorSlider
import com.wordco.clockworkandroid.core.ui.composables.DifficultySlider
import com.wordco.clockworkandroid.core.ui.util.dpScaledWith
import com.wordco.clockworkandroid.edit_session_feature.ui.SessionFormUiEvent
import com.wordco.clockworkandroid.edit_session_feature.ui.SessionFormUiState
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionForm(
    uiState: SessionFormUiState.Retrieved,
    modifier: Modifier = Modifier,
    onEvent: (SessionFormUiEvent) -> Unit,
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MM/dd/yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("hh:mm a") }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            10.dp,
            alignment = Alignment.Top
        ),
        horizontalAlignment = Alignment.Start,
    ) {

        OutlinedTextFieldButton(
            value = uiState.profileName ?: "No Template Selected",
            modifier = Modifier.fillMaxWidth(),
            label = "Template",
            onClick = { onEvent(SessionFormUiEvent.ProfileFieldClicked) },
        )

        Spacer(Modifier.height(5.dp))

        OutlinedTextField(
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                focusedIndicatorColor = MaterialTheme.colorScheme.secondary
            ),
            value = uiState.taskName,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { onEvent(SessionFormUiEvent.TaskNameChanged(it)) },
            label = {
                Text(
                    "Assignment Name",
                    style = TextStyle(
                        letterSpacing = 0.02.em // or use TextUnit(value, TextUnitType.Sp)
                    )
                )
            },
            trailingIcon = {
                if (uiState.taskName.isNotEmpty()) {
                    IconButton(
                        onClick = { onEvent(SessionFormUiEvent.TaskNameChanged("")) },
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear current template",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(23.dpScaledWith(16.sp))
                        )
                    }
                }
            }
        )

        Spacer(Modifier.height(5.dp))

        ColorSlider(
            label = "Session Color",
            value = uiState.colorSliderPos,
            onValueChange = { onEvent(SessionFormUiEvent.ColorSliderChanged(it)) },
        )

        Spacer(Modifier.height(5.dp))

        DifficultySlider(
            label = "Session Difficulty",
            value = uiState.difficulty,
            onValueChange = { onEvent(SessionFormUiEvent.DifficultySliderChanged(it)) }
        )

        Spacer(Modifier.height(5.dp))


        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextFieldButton(
                value = uiState.dueDate?.format(dateFormatter) ?: "No Due Date",
                modifier = Modifier.run {
                    if (uiState.dueDate != null) {
                        width(160.dpScaledWith(16.sp)).weight(3f)
                    } else {
                        fillMaxWidth()
                    }
                },
                label = if (uiState.dueDate == null) "Complete By" else "Complete By Date",
                onClick = { onEvent(SessionFormUiEvent.DueDateFieldClicked) },
                trailingIcon = uiState.dueDate?.let {
                    {
                        IconButton(
                            onClick = { onEvent(SessionFormUiEvent.DueDateChanged(null)) }
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear selected date",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(23.dpScaledWith(16.sp))
                            )
                        }
                    }
                }
            )

            if (uiState.dueDate != null) {
                OutlinedTextFieldButton(
                    value = uiState.dueTime!!.format(timeFormatter),
                    modifier = Modifier.width(110.dpScaledWith(16.sp))
                        .weight(2f),
                    label = "Complete By Time",
                    onClick = { onEvent(SessionFormUiEvent.DueTimeFieldClicked) },
                )
            }
        }

        Spacer(Modifier.height(5.dp))

        OutlinedTextFieldButton(
            value = uiState.estimate?.let {
                "${it.hours} hours, ${it.minutes} minutes"
            } ?: "Not Set",
            label = "Estimated Duration",
            onClick = { onEvent(SessionFormUiEvent.EstimateFieldClicked) },
            trailingIcon = if (uiState.estimate != null && uiState.isEstimateEditable) {
                {
                    IconButton(
                        onClick = { onEvent(SessionFormUiEvent.EstimateChanged(null)) }
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear estimate",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(23.dpScaledWith(16.sp))
                        )
                    }
                }
            } else {
                null
            },
            modifier = Modifier.fillMaxWidth(),
            isEnabled = uiState.isEstimateEditable
        )

        Spacer(Modifier.height(5.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextFieldButton(
                value = uiState.reminder?.scheduledDate?.format(dateFormatter) ?: "No Reminder Set",
                modifier = Modifier.run {
                    if (uiState.reminder != null) {
                        width(160.dpScaledWith(16.sp)).weight(3f)
                    } else {
                        fillMaxWidth()
                    }
                },
                label = if (uiState.reminder == null) "Remind Me At" else "Remind Me At Date",
                onClick = { onEvent(SessionFormUiEvent.ReminderDateFieldClicked) },
                trailingIcon = uiState.reminder?.let {
                    {
                        IconButton(
                            onClick = { onEvent(SessionFormUiEvent.ReminderDateChanged(null)) }
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear selected date",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(23.dpScaledWith(16.sp))
                            )
                        }
                    }
                }
            )

            if (uiState.reminder != null) {
                OutlinedTextFieldButton(
                    value = uiState.reminder.scheduledTime.format(timeFormatter),
                    modifier = Modifier.width(110.dpScaledWith(16.sp))
                        .weight(2f),
                    label = "Remind Me At Time",
                    onClick = { onEvent(SessionFormUiEvent.ReminderTimeFieldClicked) },
                )
            }
        }
    }
}