package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wordco.clockworkandroid.core.ui.composables.ColorSlider
import com.wordco.clockworkandroid.core.ui.composables.DifficultySlider
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.edit_session_feature.ui.EditTaskFormUiState
import com.wordco.clockworkandroid.edit_session_feature.ui.model.PickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskForm(
    uiState: EditTaskFormUiState,
    modifier: Modifier = Modifier,
    onShowProfilePicker: () -> Unit,
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
) {
    val dueDatePickerState = rememberDatePickerState()
    val dueTimePickerState = rememberTimePickerState(
        initialHour = uiState.dueTime?.hour ?: 0,
        initialMinute = uiState.dueTime?.minute ?: 0
    )
    val hoursList = remember { (0..99).reversed().toList() }
    val minutesList = remember { (0..99).reversed().toList() }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        OutlinedTextFieldButton(
            value = uiState.profileName ?: "No Profile Selected",
            modifier = Modifier.fillMaxWidth(),
            label = "Profile",
            onClick = onShowProfilePicker,
        )

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
            },
            trailingIcon = {
                if (uiState.taskName.isNotEmpty()) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear current name",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.combinedClickable(
                            onClick = { onTaskNameChange("") }
                        )
                    )
                }
            }
        )

        ColorSlider(
            label = "Session Color",
            value = uiState.colorSliderPos,
            onValueChange = onColorSliderChange,
        )

        DifficultySlider(
            label = "Session Difficulty",
            value = uiState.difficulty,
            onValueChange = onDifficultyChange
        )

        Row (
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextFieldButton(
                value = uiState.dueDate?.let {
                    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                    it.format(formatter)
                } ?: "Not Scheduled",
                modifier = Modifier.weight(0.45f),
                label = "Due Date",
                onClick = onShowDatePicker,
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

                OutlinedTextFieldButton(
                    value = uiState.dueTime.toString(),
                    modifier = Modifier.weight(0.35f),
                    label = "Due Time",
                    onClick = onShowTimePicker,
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
                    items = hoursList,
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
                    items = minutesList,
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
    }
}

@Preview
@Composable
private fun EditTaskFormPreview() {
    ClockworkTheme {
        EditTaskForm(
            uiState = object : EditTaskFormUiState {
                override val taskName: String = "Preview"
                override val profileName: String? = "Preview"
                override val colorSliderPos: Float
                    get() = Random.nextFloat()
                override val difficulty: Float
                    get() = Random.nextInt(0, 5).toFloat()
                override val dueDate: LocalDate? = null
                override val dueTime: LocalTime? = null
                override val currentModal: PickerModal? = null
                override val estimate: UserEstimate? = null

            },
            onShowProfilePicker = {},
            onTaskNameChange = {},
            onColorSliderChange = {},
            onDifficultyChange = {},
            onShowDatePicker = {},
            onDismissDatePicker = {},
            onDueDateChange = {},
            onShowTimePicker = {},
            onDismissTimePicker = {},
            onDueTimeChange = {},
            onEstimateChange = {}
        )
    }
}