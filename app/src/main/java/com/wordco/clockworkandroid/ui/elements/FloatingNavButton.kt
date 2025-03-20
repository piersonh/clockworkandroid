package com.wordco.clockworkandroid.ui.elements

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun FloatingNavButton(displayText: String, navController: NavController, route: String) {
    FloatingActionButton(
        onClick = { navController.navigate(route) }
    ) {
        Text(displayText)
    }

}