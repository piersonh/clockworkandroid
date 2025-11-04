package com.wordco.clockworkandroid.session_completion_feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.ui.composables.AccentRectangleTextButton
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.session_completion_feature.ui.util.toHourMinuteString
import java.time.Duration
import kotlin.math.roundToInt

@Composable
fun TaskCompletionPage(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    onEditClick: () -> Unit,
    viewModel: TaskCompletionViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TaskCompletionPage(
        uiState = uiState,
        onBackClick = onBackClick,
        onContinueClick = onContinueClick,
        onEditClick = onEditClick,
        onDeleteClick = viewModel::onDeleteClick,
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                TaskCompletionUiEvent.NavigateBack -> {
                    onBackClick()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskCompletionPage(
    uiState: TaskCompletionUiState,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {

    var isMenuExpanded by remember { mutableStateOf(false) }

    var showDeleteDialog by remember {mutableStateOf(false)}

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Session Report",
                        fontFamily = LATO,
                        fontWeight = FontWeight.Black,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        BackImage()
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                actions = {
                    if (uiState is TaskCompletionUiState.Retrieved) {
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
            )
        },
    ) { innerPadding ->
        when (uiState) {
            TaskCompletionUiState.Retrieving -> Text("Retrieving...")
            is TaskCompletionUiState.Retrieved -> Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(color = MaterialTheme.colorScheme.primary)
            ) {
                SessionReport(
                    uiState = uiState,
                    modifier = Modifier.fillMaxWidth(),
                    onContinueClick = onContinueClick
                )

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
        }
    }
}


@Composable
private fun SessionReport(
    uiState: TaskCompletionUiState.Retrieved,
    modifier: Modifier = Modifier,
    onContinueClick: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(all = 10.dp)
                .fillMaxHeight()
        ) {

            BasicText(
                text = uiState.name,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 24.sp,
                    maxFontSize = 40.sp
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
            Spacer(modifier = Modifier.weight(0.004f))

            Text(
                text = uiState.totalTime.toHourMinuteString(),
                style = TextStyle(fontSize = 90.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.weight(0.03f))

            val estimateText = if (uiState.estimate != null) {
                uiState.estimate.toHourMinuteString()
            } else {
                "No estimate provided"
            }
            Text(
                text = "You estimated: $estimateText",
                style = TextStyle(fontSize = 26.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.weight(0.03f))

            Text(
                text = "Work time: ${uiState.workTime.toHourMinuteString()}",
                style = TextStyle(fontSize = 26.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.weight(0.03f))

            Text (
                text = "Break time: ${uiState.breakTime.toHourMinuteString()}",
                style = TextStyle(fontSize = 26.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimary
            )



            uiState.totalTimeAccuracy?.let {
                Spacer(modifier = Modifier.weight(0.03f))
                Text (
                    text = "Your accuracy: ${uiState.totalTimeAccuracy.roundToInt()}%",
                    style = TextStyle(fontSize = 26.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.weight(0.03f))

            AccentRectangleTextButton(
                onClick = { /* TODO: Handle View Details */ },
                maxHeight = 58.dp,
            ) {
                Text(
                    text = "View Details",
                    fontFamily = LATO,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AccentRectangleTextButton(
                onClick = onContinueClick,
            ) {
                Text(
                    text = "Continue",
                    fontFamily = LATO,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                )
            }

            Spacer(modifier = Modifier.weight(0.04f))
        }
    }
}


@Preview
@Composable
private fun TaskCompletionPagePreview() {
    ClockworkTheme {
        TaskCompletionPage(
            uiState = TaskCompletionUiState.Retrieved(
                name = "Preview Task",
                estimate = Duration.ofMinutes(10).plusHours(1),
                workTime = Duration.ofMinutes(30),
                breakTime = Duration.ofMinutes(20),
                totalTime = Duration.ofMinutes(50),
                totalTimeAccuracy = 80.0,
            ),
            onBackClick = {},
            onContinueClick = {},
            onEditClick = {},
            onDeleteClick = {},
        )
    }
}
