package com.wordco.clockworkandroid.ui.pages

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wordco.clockworkandroid.domain.model.ExecutionStatus
import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.ui.TaskViewModel
import com.wordco.clockworkandroid.ui.elements.BackImage
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.TimeZone


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskPage(
    controller: NavHostController = rememberNavController(),
    taskViewModel: TaskViewModel
) {
    rememberCoroutineScope()
    var assignmentname by remember { mutableStateOf("Homework") }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var difficulty by remember { mutableFloatStateOf(0f) }
    var dateShown by remember { mutableStateOf(false) }
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(
        initialHour = 0,
        initialMinute = 0,
        is24Hour = true,
    )
    val brush = Brush.horizontalGradient(
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
    val sdfDate = SimpleDateFormat(
        "MM/dd/yyyy"
    )
    sdfDate.timeZone = TimeZone.getTimeZone("GMT")
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                title = {
                    Row(modifier = Modifier.padding(end = 10.dp)) {
                        IconButton(onClick = { controller.navigateUp() }) {
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

            )
        {
            OutlinedTextField(
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                value = assignmentname,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { assignmentname = it },
                label = {
                    Text(
                        "Assignment Name",
                        style = TextStyle(
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
                value = sliderPosition,
                thumb = {
                    Box(
                        Modifier
                            .size(48.dp)
                            .padding(4.dp)
                            .background(
                                Color.hsv(sliderPosition * 360, 1f, 1f),
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
                onValueChange = { sliderPosition = it }
            )
            Text(
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
                value = difficulty,
                steps = 3,
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    activeTickColor = MaterialTheme.colorScheme.primaryContainer,
                    inactiveTickColor = MaterialTheme.colorScheme.primary,
                    thumbColor = MaterialTheme.colorScheme.secondary
                ),
                onValueChange = { difficulty = it }
            )
            Text(
                textAlign = TextAlign.Left,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = "Due Date",
                style = TextStyle(
                    letterSpacing = 0.02.em // or use TextUnit(value, TextUnitType.Sp)
                )
            )
            OutlinedButton(
                onClick = { dateShown = true },
                shape = RoundedCornerShape(5.dp),
            ) {
                Text(
                    sdfDate.format(date),
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (dateShown) {
                DatePickerDialog(
                    confirmButton = {
                        TextButton(onClick = {
                            date = datePickerState.selectedDateMillis ?: 0
                            Log.println(Log.INFO, "d", date.toString())
                            dateShown = false
                        }) {
                            Text("OK", color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { dateShown = false }) {
                            Text("Cancel", color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    },
                    onDismissRequest = { dateShown = false },
                ) {
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
            TimeInput(
                state = timePickerState,
            )
            Button(onClick = {
                taskViewModel.insertTask(
                    Task(
                        taskId = 0,
                        name = assignmentname,
                        dueDate = Instant.ofEpochMilli(date),
                        difficulty = difficulty.toInt(),
                        color = Color.hsv(sliderPosition * 360, 1f, 1f),
                        status = ExecutionStatus.NOT_STARTED,
                        segments = emptyList(),
                        markers = emptyList(),
                    )
                )

            }) {
                Text("Add")
            }
        }


        Button(
            onClick = {
                controller.navigate("List") {
                    popUpTo(0)
                }
            }
        ) {}
    }
}

