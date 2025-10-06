package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wordco.clockworkandroid.core.ui.composables.ColorSlider
import com.wordco.clockworkandroid.core.ui.composables.DifficultySlider
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews
import com.wordco.clockworkandroid.edit_session_feature.ui.SessionFormUiState
import com.wordco.clockworkandroid.edit_session_feature.ui.model.Modal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.session_list_feature.ui.util.toDp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionForm(
    uiState: SessionFormUiState,
    density: Density,
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
    onShowEstimatePicker: () -> Unit,
    onDismissEstimatePicker: () -> Unit,
    onEstimateChange: (UserEstimate?) -> Unit,
) {
    val dueDatePickerState = rememberDatePickerState()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MM/dd/yyyy") }
    val dueTimePickerState = rememberTimePickerState(
        initialHour = uiState.dueTime?.hour ?: 0,
        initialMinute = uiState.dueTime?.minute ?: 0
    )
    val timeFormatter = remember { DateTimeFormatter.ofPattern("hh:mm a") }
    val hoursList = remember { (0..99).reversed().toList() }
    val minutesList = remember { (0..59).reversed().toList() }

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

        Text("${23.sp.toDp(density)}")

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextFieldButton(
                value = uiState.dueDate?.format(dateFormatter) ?: "Not Scheduled",
                modifier = Modifier.width(150.dp.times(density.fontScale)),
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
                OutlinedTextFieldButton(
                    value = uiState.dueTime!!.format(timeFormatter),
                    //modifier = Modifier.width(IntrinsicSize.Min),
                    label = "Due Time",
                    onClick = onShowTimePicker,
                )
            }
        }

//        Row (
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            OutlinedTextFieldButton(
//                value = uiState.dueDate?.format(dateFormatter) ?: "Not Scheduled",
//                modifier = Modifier.weight(0.45f),
//                label = "Due Date",
//                onClick = onShowDatePicker,
//                trailingIcon = {
//                    uiState.dueDate?.let {
//                        Icon(
//                            Icons.Default.Clear,
//                            contentDescription = "Clear selected date",
//                            tint = MaterialTheme.colorScheme.onPrimary,
//                            modifier = Modifier.combinedClickable(
//                                onClick = { onDueDateChange(null) }
//                            )
//                        )
//                    }
//                }
//            )
//
//            uiState.dueDate?.let {
//                Spacer(
//                    Modifier.weight(0.05f)
//                )
//
//                OutlinedTextFieldButton(
//                    value = uiState.dueTime!!.format(timeFormatter),
//                    modifier = Modifier.weight(0.35f),
//                    label = "Due Time",
//                    onClick = onShowTimePicker,
//                )
//            }
//        }

        Spacer(Modifier.height(5.dp))

        OutlinedTextFieldButton(
            value = uiState.estimate?.let {
                "${it.hours} hours, ${it.minutes} minutes"
            } ?: "No Estimate",
            label = "Estimated Duration",
            onClick = onShowEstimatePicker,
            trailingIcon = {
                uiState.estimate?.let {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear estimate",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.combinedClickable(
                            onClick = { onEstimateChange(null) }
                        )
                    )
                }
            }
        )

        when (uiState.currentModal) {
            Modal.Date -> {
                DatePickerDialog(
                    onDismissRequest = onDismissDatePicker,
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onDueDateChange(dueDatePickerState.selectedDateMillis)
                                onDismissDatePicker()
                            }
                        ) {
                            Text(
                                "OK",
                                fontFamily = LATO,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = onDismissDatePicker
                        ) {
                            Text(
                                "Cancel",
                                fontFamily = LATO,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                            )
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
            Modal.Time -> {
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
                            TimePicker(
                                state = dueTimePickerState,
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
                                TextButton(onClick = onDismissTimePicker) {
                                    Text(
                                        "Cancel",
                                        fontFamily = LATO,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                    )
                                }
                                TextButton(onClick = {
                                    val time = dueTimePickerState.run {
                                        LocalTime.of(hour, minute)
                                    }
                                    onDueTimeChange(time)
                                    onDismissTimePicker()
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
            Modal.Estimate -> {
                var est = uiState.estimate ?: UserEstimate(0,0)
                Dialog(
                    onDismissRequest = onDismissEstimatePicker,
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
                                text = "Select Estimated Session Duration",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Row (
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = "Hours",
                                        textAlign = TextAlign.Center,
                                        fontSize = 20.sp,
                                        fontFamily = LATO,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = TextStyle(
                                            letterSpacing = 0.02.em // or use TextUnit(value, TextUnitType.Sp)
                                        )
                                    )

                                    InfiniteCircularList(
                                        width = 40.dp,
                                        itemHeight = 60.dp,
                                        items = hoursList,
                                        initialItem = est.hours,
                                        textStyle = TextStyle(fontSize = 23.sp),
                                        textColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                        onItemSelected = { i, item ->
                                            est = est.copy(hours = item)
                                        }
                                    )
                                }


                                Spacer(modifier = Modifier.width(60.dp))

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Minutes",
                                        textAlign = TextAlign.Center,
                                        fontSize = 20.sp,
                                        fontFamily = LATO,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = TextStyle(
                                            letterSpacing = 0.02.em // or use TextUnit(value, TextUnitType.Sp)
                                        )
                                    )

                                    InfiniteCircularList(
                                        width = 40.dp,
                                        itemHeight = 60.dp,
                                        items = minutesList,
                                        initialItem = est.minutes,
                                        textStyle = TextStyle(fontSize = 23.sp),
                                        textColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                        onItemSelected = { i, item ->
                                            est = est.copy(minutes = item)
                                        }
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .height(40.dp)
                                    .fillMaxWidth()
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = onDismissEstimatePicker) {
                                    Text(
                                        "Cancel",
                                        fontFamily = LATO,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                    )
                                }
                                TextButton(onClick = {
                                    onEstimateChange(est)
                                    onDismissEstimatePicker()
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
            null -> {}
        }
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
                dueDate= null,
                dueTime = null,
                currentModal = null,
                estimate = null,
            ),
            density = LocalDensity.current,
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
            onEstimateChange = {},
            onShowEstimatePicker = {},
            onDismissEstimatePicker = {}
        )
    }
}