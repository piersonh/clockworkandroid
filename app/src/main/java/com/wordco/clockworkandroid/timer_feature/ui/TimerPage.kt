package com.wordco.clockworkandroid.timer_feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews
import com.wordco.clockworkandroid.timer_feature.ui.composables.TimeDisplay
import com.wordco.clockworkandroid.timer_feature.ui.composables.TimerControls
import kotlinx.coroutines.launch
import java.time.Duration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPage(
    // See https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#jetpack-compose
    timerViewModel: TimerViewModel,// = viewModel(factory = TimerViewModel.Factory),
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onFinishClick: () -> Unit
) {
    val uiState by timerViewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        timerViewModel.events.collect { event ->
            when (event) {
                is TimerUiEvent.ShowSnackbar -> {
                    launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
                is TimerUiEvent.NavigateBack -> {
                    onBackClick()
                }
                is TimerUiEvent.FinishSession -> {
                    onFinishClick()
                }
            }
        }
    }

    TimerPage(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onEditClick = onEditClick,
        onDeleteClick = timerViewModel::onDeleteClick,
        onInitClick = timerViewModel::initTimer,
        onBreakClick = timerViewModel::takeBreak,
        onSuspendClick = timerViewModel::suspendTimer,
        onResumeClick = timerViewModel::resumeTimer,
        onMarkClick = timerViewModel::addMark,
        onFinishClick = timerViewModel::finish,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimerPage(
    uiState: TimerUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onInitClick: () -> Unit,
    onBreakClick: () -> Unit,
    onSuspendClick: () -> Unit,
    onResumeClick: () -> Unit,
    onMarkClick: () -> Unit,
    onFinishClick: () -> Unit,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    var showDeleteDialog by remember {mutableStateOf(false)}

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Session Timer",
                        fontFamily = LATO,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        BackImage()
                    }
                },
                actions = {
                    if (uiState is TimerUiState.Shelved) {
                        Box {
                            IconButton(onClick = { isMenuExpanded = true }) {
                                Icon(
                                    painterResource(R.drawable.three_dots_vertical),
                                    contentDescription = "More options",
                                    tint = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.padding(vertical = 7.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = isMenuExpanded,
                                onDismissRequest = { isMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Edit",
                                            fontSize = 25.sp,
                                            textAlign = TextAlign.Right,
                                            fontFamily = LATO,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    },
                                    onClick = {
                                        isMenuExpanded = false
                                        onEditClick()
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Delete",
                                            fontSize = 25.sp,
                                            textAlign = TextAlign.Right,
                                            fontFamily = LATO,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    },
                                    onClick = {
                                        isMenuExpanded = false
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(30.dp)
                ) {
                    Text(
                        text = data.visuals.message,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontFamily = LATO,
                        fontSize = 24.sp,
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.TopEnd
        ) {

            when (uiState) {
                is TimerUiState.Retrieved -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 10.dp)
                    ) {
                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        BasicText(
                            text = uiState.taskName,
                            autoSize = TextAutoSize.StepBased(
                                minFontSize = 24.sp,
                                maxFontSize = 48.sp
                            ),
                            style = TextStyle(
                                fontFamily = LATO,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 48.sp
                            ),
                            modifier = Modifier.heightIn(max=60.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )


                        TimeDisplay(uiState)


                        TimerControls(
                            modifier = Modifier
                                .aspectRatio(2f)
                                .defaultMinSize(minHeight = 200.dp),
                            uiState,
                            onInitClick,
                            onBreakClick,
                            onSuspendClick,
                            onResumeClick,
                            onMarkClick = onMarkClick,
                            onFinishClick,
                        )
                    }

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = {
                                Text(
                                    "Delete Session?",
                                    fontFamily = LATO,
                                    fontWeight = FontWeight.Bold,
                                )
                            },
                            text = {
                                Text(
                                    "Are you sure about that?",
                                    fontFamily = LATO,
                                )
                           },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDeleteDialog = false
                                        onDeleteClick()
                                    },
                                ) {
                                    Text(
                                        "Confirm",
                                        fontFamily = LATO,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showDeleteDialog = false },
                                ) {
                                    Text(
                                        "Cancel",
                                        fontFamily = LATO,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            },
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                            )
                        )
                    }
                }

                TimerUiState.Retrieving -> Text("Loading task...")
            }
        }
    }
}

@AspectRatioPreviews
@Composable
private fun SuspendedTimerPagePreview() {
    ClockworkTheme {
        TimerPage(
            uiState = TimerUiState.Suspended(
                taskName = "Ooga Booga",
                elapsedSeconds = Duration.ofHours(1)
                    .plusMinutes(21)
                    .seconds.toInt(),
                false
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onInitClick = {},
            onBreakClick = {},
            onSuspendClick = {},
            onResumeClick = {},
            onMarkClick = {},
            onFinishClick = {}
        )
    }
}