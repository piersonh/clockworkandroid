package com.wordco.clockworkandroid.session_completion_feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.theme.ClockWorkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews
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
    val scrollState = rememberScrollState()
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            // Task Title Badge
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3F),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(
                    text = uiState.name,
                    color = MaterialTheme.colorScheme.onPrimary,
                    // possible autosizing needed
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

//            BasicText(
//                text = uiState.name,
//                autoSize = TextAutoSize.StepBased(
//                    minFontSize = 24.sp,
//                    maxFontSize = 40.sp
//                ),
//                style = TextStyle(
//                    fontFamily = LATO,
//                    textAlign = TextAlign.Center,
//                    color = MaterialTheme.colorScheme.onPrimaryContainer,
//                    fontSize = 48.sp
//                ),
//                modifier = Modifier.heightIn(max=60.dp),
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis,
//            )
            Spacer(modifier = Modifier.weight(0.004f))

            // Circular Progress

            if (uiState.estimate != null) {
                val estimateText = uiState.estimate.toHourMinuteString()
                val totalTimeFloat = uiState.totalTime.seconds.toFloat()
                val estimateFloat = uiState.estimate.seconds.toFloat()

                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { totalTimeFloat / estimateFloat },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.secondary,
                        strokeWidth = 14.dp,
                        trackColor = MaterialTheme.colorScheme.primaryContainer,
                        strokeCap = StrokeCap.Round
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = uiState.totalTime.toHourMinuteString(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "of $estimateText estimated",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                "No estimate provided"
            }

//            Text(
//                text = uiState.totalTime.toHourMinuteString(),
//                style = TextStyle(fontSize = 60.sp),
//                textAlign = TextAlign.Center,
//                modifier = Modifier,
//                color = MaterialTheme.colorScheme.surfaceVariant
//            )

            Spacer(modifier = Modifier.weight(0.03f))


            // Stats Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Work Time",
                    value = uiState.workTime.toHourMinuteString(),
                    icon = painterResource(id = R.drawable.running),
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    iconColor = MaterialTheme.colorScheme.secondary
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Break Time",
                    value = uiState.breakTime.toHourMinuteString(),
                    icon = painterResource(id = R.drawable.mug),
                    backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                    iconColor = MaterialTheme.colorScheme.tertiary
                )
            }
//
//            Text(
//                text = "Work time: ${uiState.workTime.toHourMinuteString()}",
//                style = TextStyle(fontSize = 26.sp),
//                textAlign = TextAlign.Center,
//                modifier = Modifier,
//                color = MaterialTheme.colorScheme.onPrimary
//            )
//
//            Spacer(modifier = Modifier.weight(0.03f))
//
//            Text (
//                text = "Break time: ${uiState.breakTime.toHourMinuteString()}",
//                style = TextStyle(fontSize = 26.sp),
//                textAlign = TextAlign.Center,
//                modifier = Modifier,
//                color = MaterialTheme.colorScheme.onPrimary
//            )



            uiState.totalTimeAccuracy?.let {
                // Accuracy Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    color = Color(0xFF8EC1FF),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = Color(0xFF3B65D7),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.bullseye),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Your Accuracy",
                                    fontSize = 14.sp,
                                    color = Color(0xFF262A31),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${uiState.totalTimeAccuracy.roundToInt()}%",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                            }
                        }
                    }
                }
//                Text (
//                    text = "Your accuracy: ${uiState.totalTimeAccuracy.roundToInt()}%",
//                    style = TextStyle(fontSize = 26.sp),
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier,
//                    color = MaterialTheme.colorScheme.onPrimary
//                )
            }

            Spacer(modifier = Modifier.weight(0.03f))

//            AccentRectangleTextButton(
//                onClick = { /* TODO: Handle View Details */ },
//                maxHeight = 58.dp,
//            ) {
//                Text(
//                    text = "View Details",
//                    fontFamily = LATO,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 36.sp
//                )
//            }

            Spacer(modifier = Modifier.height(24.dp))

//            AccentRectangleTextButton(
//                onClick = onContinueClick,
//            ) {
//                Text(
//                    text = "Continue",
//                    fontFamily = LATO,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 36.sp
//                )
//            }

            // Buttons
            TextButton(
                onClick = onContinueClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
            ) {
                Text(
                    text = "Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: Painter,
    backgroundColor: Color,
    iconColor: Color
) {
    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color = iconColor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = Color(0xFF262A31),
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        }
    }
}

@AspectRatioPreviews
@Composable
private fun TaskCompletionPagePreview() {
    ClockWorkTheme {
        TaskCompletionPage(
            uiState = TaskCompletionUiState.Retrieved(
                name = "Preview TaskPreview TaskPreview TaskPreview TaskPreview TaskPreview TaskPreview TaskPreview TaskPreview TaskPreview TaskPreview TaskPreview TaskPreview TaskPreview TaskPreview TaskPreview TaskPreview TaskPreview TaskPreview Task",
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
