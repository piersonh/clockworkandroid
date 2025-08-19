package com.wordco.clockworkandroid.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wordco.clockworkandroid.ui.PageRoutes
import com.wordco.clockworkandroid.ui.navigateToNewTask

@Composable
fun TaskBottomBar(
    navController: NavController
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(
                    20.dp,
                    alignment = Alignment.CenterHorizontally
                )
        )
        {
            CalImage()
            OutlinedButton(
                onClick = { navController.navigateToNewTask() },
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxSize(),
                contentPadding = PaddingValues(0.dp),
                border = BorderStroke(5.dp, MaterialTheme.colorScheme.onSecondary)
            )
            {

                PlusImage(Modifier
                    .aspectRatio(1f)
                    .fillMaxSize())
            }
            UserImage()
        }
    }
}