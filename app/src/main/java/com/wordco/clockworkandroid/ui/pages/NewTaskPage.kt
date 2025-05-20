@file:OptIn(ExperimentalMaterial3Api::class)

package com.wordco.clockworkandroid.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wordco.clockworkandroid.ui.elements.InfiniteCircularList

// TODO: Implement dialogs for time pickers

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewTaskPage(controller: NavHostController) {
    var taskTitle by remember { mutableStateOf("") }
    var difficulty by remember { mutableFloatStateOf(0f) }
    //Svar estimatedCompTime by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var getDateCal by remember { mutableStateOf(false) }
    var getTimeEst by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var onTaskDueDate by remember { mutableStateOf<Long?>(null) }

    var hour by remember { mutableIntStateOf(0) }
    var minute by remember { mutableIntStateOf(0) }

    Scaffold(
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: Handle Add Task Instance to List */ controller.navigate("list")}
            ) { Text("Add Task") }
        },
        modifier = Modifier.fillMaxSize(),
        content = {
            Column(
                modifier = Modifier.fillMaxSize().padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(taskTitle.ifEmpty { "Title" })
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { getDateCal = true },
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text("Due Date")
                }
                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = difficulty,
                    onValueChange = { difficulty = it },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    // NOT INCLUDING STARTING AND ENDING STEPS!!!
                    steps = 3,
                    valueRange = 0f..25f
                )
                Text( text = "Difficulty: ${(difficulty / (25f / 4)).toInt() + 1}")
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { getTimeEst = true }) {
                    Text("Estimated Completion Time")
                }


                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Enter Task Title") },
                        text = {
                            OutlinedTextField(
                                value = taskTitle,
                                onValueChange = { taskTitle = it },
                                label = { Text("Task Title") }
                            )
                        },
                        confirmButton = {
                            Button(onClick = { showDialog = false }) {
                                Text("OK")
                            }
                        }
                    )
                }
                if (getDateCal){
                    DatePickerDialog(
                        onDismissRequest = { getDateCal = false },
                        confirmButton = {
                            TextButton(onClick = {
                                onTaskDueDate = datePickerState.selectedDateMillis
                                getDateCal = false
                            }) {
                                Text("Ok")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { getDateCal = false}) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                if (getTimeEst){
                    Row (
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // List of hours
                        InfiniteCircularList(
                            width = 200.dp,
                            itemHeight = 70.dp,
                            items = (0..24).toList(),
                            initialItem = hour,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 23.sp),
                            textColor = Color.LightGray,
                            selectedTextColor = Color.Black,
                            onItemSelected = { i, item ->
                                hour = item
                            }
                        )
                        InfiniteCircularList(
                            width = 200.dp,
                            itemHeight = 70.dp,
                            items = (0..59).toList(),
                            initialItem = minute,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 23.sp),
                            textColor = Color.LightGray,
                            selectedTextColor = Color.Black,
                            onItemSelected = { i, item ->
                                minute = item
                            }
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun NewTaskPagePreview() {
    val mockNavController = rememberNavController()
    NewTaskPage(controller = mockNavController)
}
