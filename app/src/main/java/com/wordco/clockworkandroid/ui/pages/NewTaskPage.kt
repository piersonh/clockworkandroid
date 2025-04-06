package com.wordco.clockworkandroid.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlin.math.roundToInt

// TODO: Add buttons to bring the time and date pickers up
// TODO: Implement dialogs for time and date pickers

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewTaskPage(controller: NavHostController) {
    var taskTitle by remember { mutableStateOf("") }
    var formattedDate by remember { mutableStateOf("") }
    var difficulty by remember { mutableFloatStateOf(0f) }
    var estimatedCompTime by remember { mutableStateOf("") }

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
                Button(onClick = { taskTitle = setTaskTitle(taskTitle) }) {
                    Text(taskTitle.ifEmpty { "Title" })
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* TODO: Set Formatted Due Date */ }) {
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
                    steps = 5,
                    valueRange = 0f..50f
                )
                Text( text = "Difficulty: ${difficulty.roundToInt()}")

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* TODO: Set Estimated Completion Time in HH:MM format */ }) {
                    Text("Estimated Completion Time")
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

fun setTaskTitle(taskTitle: String): String {
    print(taskTitle)
    return taskTitle
}