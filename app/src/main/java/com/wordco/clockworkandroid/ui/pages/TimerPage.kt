package com.wordco.clockworkandroid.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wordco.clockworkandroid.domain.model.Timer
import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.ui.elements.BackImage
import com.wordco.clockworkandroid.ui.elements.TimeDisplay
import com.wordco.clockworkandroid.ui.elements.TimerControls
import com.wordco.clockworkandroid.ui.theme.LATO


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPage(
    timer: Timer,
    navController: NavController,
    task: MutableState<Task?>)
{
    val state by timer.state.collectAsState()

    // FIXME
    timer.setTimer(
        task.value?.workTime?.toMillis()?.toInt()?:0
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                title = {
                    Row(modifier = Modifier.padding(end = 10.dp)) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            BackImage()
                        }

                        Spacer(Modifier.weight(1f))
                        if (state == Timer.State.INIT) {
                            Text(
                                modifier = Modifier.align(alignment = Alignment.CenterVertically),
                                text = "Edit",
                                style = TextStyle(fontSize = 25.sp),
                                textAlign = TextAlign.Right,
                                fontFamily = LATO,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            )
        }
    ) {
        innerPadding -> Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.TopEnd
        ) {
            // FIXME: make it not this way
            task.value?.let {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .padding(top = 50.dp)
                ) {
                    Text(
                        // FIXME
                        text = task.value!!.name,
                        style = TextStyle(fontSize = 48.sp),
                        modifier = Modifier,
                        fontFamily = LATO,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
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
            } ?: Text("ERROR: NO TASK SELECTED")
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