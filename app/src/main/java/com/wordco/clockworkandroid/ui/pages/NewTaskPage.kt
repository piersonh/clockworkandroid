package com.wordco.clockworkandroid.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

// TODO: Add buttons to bring the time and date pickers up
// TODO: Implement dialogs for time and date pickers

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewTaskPage(controller: NavHostController) = Scaffold(
    floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center,
    floatingActionButton = {
        ExtendedFloatingActionButton(onClick = { /* TODO: Handle Add Task */ }) { Text("Add Task") }
    },
    modifier = Modifier.fillMaxSize(),
    content = {
        Text("New Task Page")
    }
)



@Preview
@Composable
fun NewTaskPagePreview() {
    val mockNavController = rememberNavController()
    NewTaskPage(controller = mockNavController)
}