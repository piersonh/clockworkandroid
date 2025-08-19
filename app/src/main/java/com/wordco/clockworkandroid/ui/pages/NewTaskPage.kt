package com.wordco.clockworkandroid.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.domain.model.ExecutionStatus
import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.ui.NewTaskUiState
import com.wordco.clockworkandroid.ui.NewTaskViewModel
import com.wordco.clockworkandroid.ui.TaskViewModel
import com.wordco.clockworkandroid.ui.elements.BackImage
import com.wordco.clockworkandroid.ui.elements.DateTimePickerDialog
import com.wordco.clockworkandroid.ui.elements.InfiniteCircularList
import com.wordco.clockworkandroid.util.asTaskDueFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.Date


@Composable
fun NewTaskPage (
    onBackClick: () -> Unit,
    newTaskViewModel: NewTaskViewModel
) {
    val uiState by newTaskViewModel.uiState.collectAsStateWithLifecycle()

    NewTaskPage(
        uiState = uiState,
        onBackClick = onBackClick,
        onTaskNameChange = newTaskViewModel::onTaskNameChange
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewTaskPage(
    uiState: NewTaskUiState,
    onBackClick: () -> Unit,
    onTaskNameChange: (String) -> Unit,
    onColorSliderChange: (Float) -> Unit,
    onDifficultyChange: (Float) -> Unit,
    onShowDatePicker: () -> Unit,
    onDismissDatePicker: () -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onShowTimePicker: () -> Unit,
    onDismissTimePicker: () -> Unit,
    onDueTimeChange: (LocalTime) -> Unit
) {
    val dueDatePickerState = rememberDatePickerState()
    val dueTimePickerState = rememberTimePickerState()
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary, topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ), title = {
                    Row(modifier = Modifier.padding(end = 10.dp)) {
                        IconButton(onClick = onBackClick) {
                            BackImage()
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 30.dp, vertical = 20.dp),
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
                value = uiState.difficulty, steps = 3, colors = SliderDefaults.colors(
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
                        val formatter = SimpleDateFormat("MM/dd/yyyy")

                        // Format the date into a string and return it
                        formatter.format(it)
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
                NewTaskViewModel.PickerModal.DATE -> {
                    DatePickerDialog(
                        onDismissRequest = onDismissDatePicker,
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    onDueDateChange(dueDatePickerState.selectedDateMillis)
                                    onDismissDatePicker()
                                }
//                                onClick = {
//                                    dueDate = dueDatePickerState.selectedDateMillis?.let {
//                                        Instant.ofEpochMilli(
//                                            // Convert to local time
//                                            it - (ZonedDateTime.now().offset.totalSeconds * 1000)
//                                        )
//                                    }
//                                    pickerState = 0
//                                }
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
                NewTaskViewModel.PickerModal.TIME -> {
                    Dialog(
                        onDismissRequest = { pickerState = 0 },
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
                                    TextButton(onClick = { pickerState = 0 }) { Text("Cancel") }
                                    TextButton(onClick = {
                                        dueTime = dueTimePickerState.minute * 60 + dueTimePickerState.hour * 3600
                                        pickerState = 0
                                    }
                                    ) { Text("OK") }
                                }
                            }
                        }
                    }
                }
                null -> {}
            }


//            if (isDateTimePickerShown) {
//                DateTimePickerDialog(
//                    onDismiss = { isDateTimePickerShown = false },
//                    onConfirm = {
//                        dueDateTime = dueDatePickerState.selectedDateMillis?.let {
//                           Instant.ofEpochMilli(
//                                // Convert to local time
//                                it - (ZonedDateTime.now().offset.totalSeconds * 1000)
//                            ).plusSeconds(
//                                (dueTimePickerState.minute * 60 + dueTimePickerState.hour * 3600)
//                                    .toLong()
//                            )
//                        }
//
//                        isDateTimePickerShown = false
//                    },
//                    datePickerState = dueDatePickerState,
//                    timePickerState = dueTimePickerState
//                )
//
//            }


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
            Row(horizontalArrangement =
                Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ){
                InfiniteCircularList(
                    width = 40.dp,
                    itemHeight = 60.dp,
                    items = (0..24).toList(),
                    initialItem = hour,
                    textStyle = TextStyle(fontSize = 23.sp),
                    textColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    onItemSelected = { i, item ->
                        hour = item
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
                    initialItem = minute,
                    textStyle = TextStyle(fontSize = 23.sp),
                    textColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    onItemSelected = { i, item ->
                        minute = item
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

            Button(colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,

            ),
                onClick = {
                taskViewModel.insertTask(
                    Task(
                        taskId = 0,
                        name = taskName,
                        dueDate = dueDateTime,
                        difficulty = difficulty.toInt(),
                        color = Color.hsv(colorSliderPos * 360, 1f, 1f),
                        status = ExecutionStatus.NOT_STARTED,
                        segments = emptyList(),
                        markers = emptyList(),
                    )
                )

                onBackClick()

            }) {
                Text("Add",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)

                )
            }
        }
    }
}

