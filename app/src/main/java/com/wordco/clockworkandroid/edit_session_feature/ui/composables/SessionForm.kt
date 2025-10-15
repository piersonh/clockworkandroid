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
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews
import com.wordco.clockworkandroid.core.ui.util.dpScaledWith
import com.wordco.clockworkandroid.edit_session_feature.ui.SessionFormUiState
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionForm(
    uiState: SessionFormUiState,
    modifier: Modifier = Modifier,
    onShowProfilePicker: () -> Unit,
    onTaskNameChange: (String) -> Unit,
    onColorSliderChange: (Float) -> Unit,
    onDifficultyChange: (Float) -> Unit,
    onShowDatePicker: () -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onShowTimePicker: () -> Unit,
    onShowEstimatePicker: () -> Unit,
    onEstimateChange: (UserEstimate?) -> Unit,
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
            value = uiState.profileName ?: "No Profile Selected",
            modifier = Modifier.fillMaxWidth(),
            label = "Profile",
            onClick = onShowProfilePicker,
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
            onValueChange = onTaskNameChange,
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
                        onClick = { onTaskNameChange("") }
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear current profile",
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
            onValueChange = onColorSliderChange,
        )

        Spacer(Modifier.height(5.dp))

        DifficultySlider(
            label = "Session Difficulty",
            value = uiState.difficulty,
            onValueChange = onDifficultyChange
        )

        Spacer(Modifier.height(5.dp))


        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextFieldButton(
                value = uiState.dueDate?.format(dateFormatter) ?: "Not Scheduled",
                modifier = Modifier.run {
                    if (uiState.dueDate != null) {
                        width(160.dpScaledWith(16.sp)).weight(3f)
                    } else {
                        fillMaxWidth()
                    }
                },
                label = "Due Date",
                onClick = onShowDatePicker,
                trailingIcon = uiState.dueDate?.let {
                    {
                        IconButton(
                            onClick = { onDueDateChange(null) }
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
                    label = "Due Time",
                    onClick = onShowTimePicker,
                )
            }
        }

        Spacer(Modifier.height(5.dp))

        OutlinedTextFieldButton(
            value = uiState.estimate?.let {
                "${it.hours} hours, ${it.minutes} minutes"
            } ?: "No Estimate",
            label = "Estimated Duration",
            onClick = onShowEstimatePicker,
            trailingIcon = if(uiState.estimate != null && uiState.isEstimateEditable) {
                {
                    IconButton(
                        onClick = { onEstimateChange(null) }
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
    }
}

@AspectRatioPreviews
@Composable
private fun EditTaskFormPreview() {
    ClockworkTheme {
        SessionForm(
            uiState = SessionFormUiState (
                taskName = "Preview",
                profileName = "Preview",
                colorSliderPos = Random.nextFloat(),
                difficulty = Random.nextInt(0, 5).toFloat(),
                dueDate = null,
                dueTime = null,
                estimate = null,
                isEstimateEditable = false,
            ),
            onShowProfilePicker = {},
            onTaskNameChange = {},
            onColorSliderChange = {},
            onDifficultyChange = {},
            onShowDatePicker = {},
            onDueDateChange = {},
            onShowTimePicker = {},
            onEstimateChange = {},
            onShowEstimatePicker = {},
        )
    }
}