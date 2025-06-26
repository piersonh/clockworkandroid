package com.wordco.clockworkandroid.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.wordco.clockworkandroid.ui.elements.FloatingNavButton

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewTaskPage(controller: NavHostController) = Scaffold(
    floatingActionButton = {
        FloatingNavButton(
            "Back",
            controller,
            "list"
        )
    }
) {
    Text("PLACEHOLDER TEST", modifier = Modifier.fillMaxSize())
    Button(
        onClick = {
            controller.navigate("List") {
                popUpTo(0)
            }
        }
    ){}
}