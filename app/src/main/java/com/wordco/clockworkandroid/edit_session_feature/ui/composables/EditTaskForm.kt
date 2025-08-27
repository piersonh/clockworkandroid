package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wordco.clockworkandroid.edit_session_feature.ui.EditTaskFormUiState
import com.wordco.clockworkandroid.edit_session_feature.ui.PickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.UserEstimate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskForm(
    uiState: EditTaskFormUiState,
    modifier: Modifier = Modifier,
    onTaskNameChange: (String) -> Unit,
    onColorSliderChange: (Float) -> Unit,
    onDifficultyChange: (Float) -> Unit,
    onShowDatePicker: () -> Unit,
    onDismissDatePicker: () -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onShowTimePicker: () -> Unit,
    onDismissTimePicker: () -> Unit,
    onDueTimeChange: (LocalTime) -> Unit,
    onEstimateChange: (UserEstimate) -> Unit,
    confirmButton: @Composable (() -> Unit)
) {
    val dueDatePickerState = rememberDatePickerState()
    val dueTimePickerState = rememberTimePickerState(
        initialHour = uiState.dueTime?.hour ?: 0,
        initialMinute = uiState.dueTime?.minute ?: 0
    )
    val brush = remember {
        Brush.horizontalGradient(
            listOf(
                Color.hsv(0f, 1f, 1f),
                Color.hsv(60f, 1f, 1f),
                Color.hsv(120f, 1f, 1f),
                Color.hsv(180f, 1f, 1f),
                Color.hsv(240f, 1f, 1f),
                Color.hsv(300f, 1f, 1f),
                Color.hsv(360f, 1f, 1f)
            )
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
                    "Assignment Name", style = TextStyle(
                        letterSpacing = 0.02.em // or use TextUnit(value, TextUnitType.Sp)
                    )
                )
            }
        )


        Text(
            textAlign = TextAlign.Left,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = "Assignment Color",
            style = TextStyle(
                letterSpacing = 0.02.em // or use TextUnit(value, TextUnitType.Sp)
            )
        )


        Slider(
            value = uiState.colorSliderPos,
            thumb = {
                Box(
                    Modifier
                        .size(48.dp)
                        .padding(4.dp)
                        .background(
                            Color.hsv(
                                uiState.colorSliderPos * 360, 1f, 1f
                            ),
                            RoundedCornerShape(50.dp)
                        )
                        .border(
                            5.dp,
                            MaterialTheme.colorScheme.onPrimary,
                            RoundedCornerShape(50.dp)
                        )
                )
            },
            track = {
                Box(
                    Modifier
                        .padding(4.dp)
                        .background(brush, RoundedCornerShape(10.dp))
                        .height(10.dp)
                        .fillMaxWidth()
                )
            },
            onValueChange = onColorSliderChange
        )


        Text (
            textAlign = TextAlign.Left,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = "Assignment Difficulty",
            style = TextStyle(
                letterSpacing = 0.02.em // or use TextUnit(value, TextUnitType.Sp)
            )
        )

        Slider(
            value = uiState.difficulty,
            steps = 3,
            valueRange = 0f..4f,
            colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
                activeTickColor = MaterialTheme.colorScheme.primaryContainer,
                inactiveTickColor = MaterialTheme.colorScheme.primary,
                thumbColor = MaterialTheme.colorScheme.secondary
            ),
            onValueChange = onDifficultyChange
        )

        Row (
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                // override the disabled colors
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = Color.Transparent,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledSuffixColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                value = uiState.dueDate?.let {
                    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                    it.format(formatter)
                } ?: "Not Scheduled",
                enabled = false,
                modifier = Modifier
                    .combinedClickable(
                        onClick = onShowDatePicker
                    )
                    .weight(0.45f),
                label = {
                    Text(
                        "Due Date", style = TextStyle(
                            letterSpacing = 0.02.em
                        )
                    )
                },
                onValueChange = { },
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    uiState.dueDate?.let {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear selected date",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.combinedClickable(
                                onClick = { onDueDateChange(null) }
                            )
                        )
                    }
                }
            )

            uiState.dueDate?.let {
                Spacer(
                    Modifier.weight(0.05f)
                )
                OutlinedTextField(
                    // override the disabled colors
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = Color.Transparent,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledSuffixColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    value = uiState.dueTime.toString(),
                    enabled = false,
                    modifier = Modifier
                        .combinedClickable(
                            onClick = onShowTimePicker
                        )
                        .weight(0.35f),
                    label = {
                        Text(
                            "Due Time", style = TextStyle(
                                letterSpacing = 0.02.em
                            )
                        )
                    },
                    onValueChange = { },
                    singleLine = true,
                    readOnly = true,
                )
            }
        }



        when (uiState.currentModal) {
            PickerModal.DATE -> {
                DatePickerDialog(
                    onDismissRequest = onDismissDatePicker,
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDueDateChange(dueDatePickerState.selectedDateMillis)
                                onDismissDatePicker()
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = onDismissDatePicker
                        ) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(
                        state = dueDatePickerState,
                        colors = DatePickerDefaults.colors(
                            selectedDayContainerColor = MaterialTheme.colorScheme.secondary,
                            todayContentColor = MaterialTheme.colorScheme.secondary,
                            todayDateBorderColor = MaterialTheme.colorScheme.secondary,
                            selectedDayContentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                }
            }
            PickerModal.TIME -> {
                Dialog(
                    onDismissRequest = onDismissTimePicker,
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
                            TimePicker(state = dueTimePickerState)
                            Row(
                                modifier = Modifier
                                    .height(40.dp)
                                    .fillMaxWidth()
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = onDismissTimePicker) { Text("Cancel") }
                                TextButton(onClick = {
                                    val time = dueTimePickerState.run {
                                        LocalTime.of(hour, minute)
                                    }
                                    onDueTimeChange(time)
                                    onDismissTimePicker()
                                }
                                ) { Text("OK") }
                            }
                        }
                    }
                }
            }
            null -> {}
        }


        Text(
            textAlign = TextAlign.Left,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = "Estimated Time",
            style = TextStyle(
                letterSpacing = 0.02.em // or use TextUnit(value, TextUnitType.Sp)
            )
        )

        uiState.estimate?.let {
                est ->
            Row(horizontalArrangement =
                Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ){
                InfiniteCircularList(
                    width = 40.dp,
                    itemHeight = 60.dp,
                    items = (0..99).toList(),
                    initialItem = est.hours,
                    textStyle = TextStyle(fontSize = 23.sp),
                    textColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    onItemSelected = { i, item ->
                        onEstimateChange(est.copy(hours = item))
                    }
                )
                Text(
                    textAlign = TextAlign.Left,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(horizontal = 5.dp),
                    text = "hours",
                    style = TextStyle(
                        letterSpacing = 0.02.em // or use TextUnit(value, TextUnitType.Sp)
                    )
                )
                InfiniteCircularList(
                    width = 40.dp,
                    itemHeight = 70.dp,
                    items = (0..59).toList(),
                    initialItem = est.minutes,
                    textStyle = TextStyle(fontSize = 23.sp),
                    textColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    onItemSelected = { i, item ->
                        onEstimateChange(est.copy(minutes = item))
                    }
                )
                Text(
                    textAlign = TextAlign.Left,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(horizontal = 5.dp),
                    text = "minutes",
                    style = TextStyle(
                        letterSpacing = 0.02.em // or use TextUnit(value, TextUnitType.Sp)
                    )
                )
            }
        }


        confirmButton()
    }

}