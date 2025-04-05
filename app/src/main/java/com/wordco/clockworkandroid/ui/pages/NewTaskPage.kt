package com.wordco.clockworkandroid.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

// TODO: Add buttons to bring the time and date pickers up
// TODO: Implement dialogs for time and date pickers

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewTaskPage(controller: NavHostController) = Scaffold(
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
            Button(onClick = { /* TODO: Set Task Title */ }) {
                Text("Title")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* TODO: Set Formatted Due Date */ }) {
                Text("Due Date")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* TODO: Set Difficulty */ }) {
                Text("Difficulty")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* TODO: Set Estimated Completion Time in HH:MM format */ }) {
                Text("Estimated Completion Time")
            }
        }
    }
)


@Preview
@Composable
fun NewTaskPagePreview() {
    val mockNavController = rememberNavController()
    NewTaskPage(controller = mockNavController)
}