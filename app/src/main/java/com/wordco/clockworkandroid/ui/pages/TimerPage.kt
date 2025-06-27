package com.wordco.clockworkandroid.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.ui.TimerState
import com.wordco.clockworkandroid.ui.TimerViewModel
import com.wordco.clockworkandroid.ui.elements.BackImage
import com.wordco.clockworkandroid.ui.elements.TimeDisplay
import com.wordco.clockworkandroid.ui.elements.TimerControls
import com.wordco.clockworkandroid.ui.theme.LATO


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPage(
    timerViewModel: TimerViewModel,
    onBackClick: () -> Unit,
) {
    val task by timerViewModel.loadedTask.observeAsState()
    val secondsElapsed by timerViewModel.secondsElapsed.observeAsState()
    val timerState by timerViewModel.state.observeAsState()

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
                        IconButton(onClick = onBackClick) {
                            BackImage()
                        }

                        Spacer(Modifier.weight(1f))
                        if (timerState == TimerState.WAITING) {
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.TopEnd
        ) {


            task?.let {
                // FIXME: make it not this way
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .padding(top = 50.dp)
                ) {
                    Text(
                        // FIXME
                        text = task!!.name,
                        style = TextStyle(fontSize = 48.sp),
                        modifier = Modifier,
                        fontFamily = LATO,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    TimeDisplay(
                        timerState = timerState,
                        secondsElapsed = secondsElapsed
                    )


                    // TIMER CONTROLS
                    TimerControls(
                        modifier = Modifier
                            .aspectRatio(2f)
                            .padding(10.dp)
                            .defaultMinSize(minHeight = 200.dp),
                        timerState,
                        onStartClick = { timerViewModel.startTimer() },
                        onBreakClick = { timerViewModel.takeBreak() },
                        onSuspendClick = { timerViewModel.suspendTimer() },
                        onResumeClick = { timerViewModel.startTimer() },
                        onMarkClick = { timerViewModel.addMark() },
                        onFinishClick = { timerViewModel.finish() },
                    )
                }
            } ?: Text("Loading task...")

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