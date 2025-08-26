package com.wordco.clockworkandroid.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TaskCompletionPage(
    //navController: NavController,
    //taskViewModel: TaskViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center

    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(all = 10.dp)
        ) {

            Text(
                // FIXME
                text = "",//taskViewModel.currentTask!!.name,
                style = TextStyle(fontSize = 40.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.weight(0.004f))

            Text(
                text = "Completed!",
                style = TextStyle(fontSize = 26.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.weight(0.03f))

            Text(
                // FIXME
                text = "",//taskViewModel.currentTask!!.workTime.asHHMM(),
                style = TextStyle(fontSize = 90.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.weight(0.03f))

            Text(
                text = "You estimated",
                style = TextStyle(fontSize = 26.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                // TODO: app estimation
                text = "TODO",
                style = TextStyle(fontSize = 34.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.weight(0.03f))

            Text(
                text = "--% overestimate",
                style = TextStyle(fontSize = 26.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.weight(0.01f))

            Text(
                text = "--% improvement from recent averages",
                style = TextStyle(fontSize = 26.sp), maxLines = 2,
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.weight(0.03f))


            Button(
                onClick = { /* TODO: Handle View Details */ },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20),
                modifier = Modifier.fillMaxWidth(0.5f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = White
                )
            ) {
                Text(
                    text = "View Details",
                    style = TextStyle(fontSize = 24.sp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Button(
                onClick = { /*navController.navigate(PageRoutes.TaskList) */ },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20),
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = White
                )
            ) {
                Text(
                    text = "Continue",
                    style = TextStyle(fontSize = 40.sp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.weight(0.04f))
        }

    }
}



/*
@Preview
@Composable
fun TaskCompletionPagePreview() {
    TaskCompletionPage()
}
*/