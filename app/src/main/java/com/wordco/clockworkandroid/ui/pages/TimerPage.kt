package com.wordco.clockworkandroid.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.wordco.clockworkandroid.model.Task
import com.wordco.clockworkandroid.model.Timer
import com.wordco.clockworkandroid.ui.LATO
import com.wordco.clockworkandroid.ui.elements.TimeDisplay
import com.wordco.clockworkandroid.ui.elements.TimerControls


@Composable
fun TimerPage(timer: Timer, navController: NavController, task: MutableState<Task>) {
    val state by timer.state.collectAsState()
    timer.setTimer(task.value.workTime)
    Box(
        modifier =  Modifier
            .fillMaxWidth()
            .background(color = Color.hsl(256f, 0.34f, 0.48f))
            .fillMaxHeight(0.1f)
            .height(
                WindowInsets.systemBars.getTop(LocalDensity.current).dp
            )

    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(vertical = 20.dp, horizontal = 10.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            Row(modifier = Modifier) {
                Text(text = "Back",
                    style = TextStyle(fontSize = 30.sp),
                    fontFamily = LATO,
                    color = Color.White,
                    modifier = Modifier.clickable { navController.navigateUp() }
                )
                Spacer(Modifier.weight(1f))
                if (state == Timer.State.INIT) {
                    Text(
                        text = "Edit",
                        style = TextStyle(fontSize = 30.sp),
                        textAlign = TextAlign.Right,
                        fontFamily = LATO,
                        color = Color.White
                    )
                }
            }


            Text(
                text = task.value.name,
                style = TextStyle(fontSize = 48.sp),
                modifier = Modifier,
                fontFamily = LATO,
                textAlign = TextAlign.Center
            )

            TimeDisplay(timer, modifier = Modifier)


            TimerControls(
                timer,
                modifier = Modifier
                    .padding(10.dp)
                    .defaultMinSize(minHeight = 200.dp),
                navController = navController
            )
        }
    }
}


/*@Preview(showBackground = true, backgroundColor = 0xcccccccc)
@Composable
private fun TimerPagePreview() {
    val navController = rememberNavController()
    TimerPage(Timer(Timer.State.INIT), navController)
}

@Preview
@Composable
private fun TimerPagePreview2() {
    val navController = rememberNavController()
    TimerPage(Timer(Timer.State.RUNNING), navController)
}

@Preview
@Composable
private fun TimerPagePreview3(){
    val navController = rememberNavController()
    TimerPage(Timer(Timer.State.PAUSED), navController)
}
*/