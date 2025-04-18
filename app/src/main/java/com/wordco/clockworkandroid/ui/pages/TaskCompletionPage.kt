package com.wordco.clockworkandroid.ui.pages
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wordco.clockworkandroid.model.Task
import com.wordco.clockworkandroid.model.Timer
import java.util.Locale

@Composable
fun TaskCompletionPage(navController: NavController, task: MutableState<Task>) {
    Box(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(10.dp), contentAlignment = Alignment.Center
    ) {
        Column (
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = task.value.name,
                style = TextStyle(fontSize = 40.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.weight(0.004f))

            Text(
                text = "Completed!",
                style = TextStyle(fontSize = 26.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.weight(0.03f))

            Text(
                text = String.format(
                    Locale.getDefault(), "%02d:%02d",
                    task.value.workTime/3600 , (task.value.workTime % 3600) / 60
                ),
                style = TextStyle(fontSize = 90.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.weight(0.03f))

            Text(
                text = "You estimated",
                style = TextStyle(fontSize = 26.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier
            )

            Text(
                 text = String.format(
                    Locale.getDefault(), "%02d:%02d",
                    task.value.estimated/3600 , (task.value.estimated % 3600) / 60
                ),
                style = TextStyle(fontSize = 34.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.weight(0.03f))

            Text(
                text = "--% overestimate",
                style = TextStyle(fontSize = 26.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.weight(0.01f))

            Text(
                text = "--% improvement from recent averages",
                style = TextStyle(fontSize = 26.sp), maxLines = 2,
                textAlign = TextAlign.Center,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.weight(0.03f))


            Button(
                onClick = { /* TODO: Handle View Details */ },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20),
                modifier = Modifier.fillMaxWidth(0.5f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = White
                )
            ) {
                Text(
                    text = "View Details",
                    style = TextStyle(fontSize = 24.sp), color = Black
                )
            }

            Button(
                onClick = { navController.navigate("List") },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20),
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = White
                )
            ) {
                Text(
                    text = "Continue",
                    style = TextStyle(fontSize = 40.sp), color = Black
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