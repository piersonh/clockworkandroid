package com.wordco.clockworkandroid.session_editor_feature.ui.main_form

import android.content.ClipData
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.wordco.clockworkandroid.core.ui.composables.AccentRectangleTextButton
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.composables.DiscardAlert
import com.wordco.clockworkandroid.core.ui.composables.ErrorReport
import com.wordco.clockworkandroid.core.ui.composables.SpinningLoader
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.DatePickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.EstimatePickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.TimerPickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.rememberEstimatePickerState
import com.wordco.clockworkandroid.edit_session_feature.ui.model.SessionFormModal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.util.toEstimate
import com.wordco.clockworkandroid.session_editor_feature.ui.main_form.components.SessionForm
import kotlinx.coroutines.launch
import java.time.ZoneOffset

@Composable
fun SessionFormPage(
    onBackClick: () -> Unit,
    viewModel: SessionFormViewModel,
    onNavigateToProfilePicker: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    // see https://stackoverflow.com/questions/79692173/how-to-resolve-deprecated-clipboardmanager-in-jetpack-compose
    val clipboard = LocalClipboard.current

    LaunchedEffect(viewModel.uiEffect, lifecycleOwner) {
        // flowWithLifecycle ensures collection stops when app is in background
        viewModel.uiEffect
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    SessionFormUiEffect.NavigateBack -> {
                        onBackClick()
                    }
                    is SessionFormUiEffect.ShowSnackbar -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = effect.message,
                                //actionLabel = effect.actionLabel,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }

                    is SessionFormUiEffect.CopyToClipboard -> {
                        coroutineScope.launch {
                            val clipData = ClipData.newPlainText(
                                effect.content,
                                effect.content
                            )
                            clipboard.setClipEntry(clipData.toClipEntry())
                        }
                    }

                    is SessionFormUiEffect.NavigateToProfilePicker -> {
                        onNavigateToProfilePicker()
                    }
                }
            }
    }

    SessionFormPageContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionFormPageContent(
    uiState: SessionFormUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (SessionFormUiEvent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        uiState.title,
                        fontFamily = LATO,
                        fontWeight = FontWeight.Black,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onEvent(SessionFormUiEvent.BackClicked)
                        }
                    ) {
                        BackImage()
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
            )
        },
        bottomBar = {
            if (uiState is SessionFormUiState.Retrieved) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        AccentRectangleTextButton(
                            onClick = {
                                onEvent(SessionFormUiEvent.SaveClicked)
                            },
                            maxHeight = 56.dp,
                            aspectRatio = 1.8f
                        ) {
                            Text(
                                "Save",
                                fontFamily = LATO,
                                fontWeight = FontWeight.Bold,
                                fontSize = 25.sp,
                            )
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when(uiState) {
                is SessionFormUiState.Retrieving -> {
                    SpinningLoader()
                }
                is SessionFormUiState.Error -> {
                    Box(
                        modifier = Modifier.padding(top = 40.dp)
                    ) {
                        ErrorReport(
                            uiState.header,
                            uiState.message,
                            onCopyErrorInfoClick = { onEvent(SessionFormUiEvent.CopyErrorClicked) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 30.dp)
                        )
                    }
                }
                is SessionFormUiState.Retrieved -> {
                    BackHandler(enabled = true) {
                        onEvent(SessionFormUiEvent.BackClicked)
                    }

                    val scrollState = rememberScrollState()

                    SessionForm(
                        uiState = uiState,
                        modifier = Modifier
                            .padding(
                                horizontal = 30.dp,
                                vertical = 20.dp
                            )
                            .verticalScroll(scrollState),
                        onEvent = onEvent,
                    )

                    ModalManager(
                        uiState = uiState,
                        onEvent = onEvent,
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalManager(
    uiState: SessionFormUiState.Retrieved,
    onEvent: (SessionFormUiEvent) -> Unit,
) {
    val modal = uiState.currentModal ?: return

    when (modal) {
        SessionFormModal.Discard -> {
            DiscardAlert(
                onDismiss = { onEvent(SessionFormUiEvent.ModalDismissed) },
                onConfirm = { onEvent(SessionFormUiEvent.DiscardConfirmed) }
            )
        }

        SessionFormModal.DueDate -> {
            val initialMillis = uiState.dueDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()

            key(initialMillis) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = initialMillis
                )

                DatePickerModal(
                    datePickerState = datePickerState,
                    onValueChange = { onEvent(SessionFormUiEvent.DueDateChanged(it)) },
                    onDismissRequest = { onEvent(SessionFormUiEvent.ModalDismissed) },
                )
            }
        }

        SessionFormModal.DueTime -> {
            val initialHour = uiState.dueTime?.hour ?: 0
            val initialMinute = uiState.dueTime?.minute ?: 0

            key(initialHour, initialMinute) {
                val timePickerState = rememberTimePickerState(
                    initialHour = initialHour,
                    initialMinute = initialMinute
                )

                TimerPickerModal(
                    timePickerState = timePickerState,
                    onValueChange = { onEvent(SessionFormUiEvent.DueTimeChanged(it)) },
                    onDismissRequest = { onEvent(SessionFormUiEvent.ModalDismissed) },
                )
            }
        }

        SessionFormModal.Estimate -> {
            key(uiState.estimate, uiState.averageSessionDuration) {
                val estimatePickerState = rememberEstimatePickerState(
                    initialValue = uiState.estimate
                        ?: uiState.averageSessionDuration?.toEstimate()
                        ?: UserEstimate(0, 0)
                )

                EstimatePickerModal(
                    estimatePickerState = estimatePickerState,
                    onValueChange = { onEvent(SessionFormUiEvent.EstimateChanged(it)) },
                    onDismissRequest = { onEvent(SessionFormUiEvent.ModalDismissed) },
                    averageSessionDuration = uiState.averageSessionDuration,
                    averageEstimateError = uiState.averageEstimateError,
                )
            }
        }

        SessionFormModal.ReminderDate -> {
            val initialMillis = uiState.reminder?.scheduledDate
                ?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()

            key(initialMillis) {
                val reminderDatePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = initialMillis
                )

                DatePickerModal(
                    datePickerState = reminderDatePickerState,
                    onValueChange = { onEvent(SessionFormUiEvent.ReminderDateChanged(it)) },
                    onDismissRequest = { onEvent(SessionFormUiEvent.ModalDismissed) },
                )
            }
        }

        SessionFormModal.ReminderTime -> {
            val initialHour = uiState.reminder?.scheduledTime?.hour ?: 0
            val initialMinute = uiState.reminder?.scheduledTime?.minute ?: 0

            key(initialHour, initialMinute) {
                val reminderTimePickerState = rememberTimePickerState(
                    initialHour = initialHour,
                    initialMinute = initialMinute
                )

                TimerPickerModal(
                    timePickerState = reminderTimePickerState,
                    onValueChange = { onEvent(SessionFormUiEvent.ReminderTimeChanged(it)) },
                    onDismissRequest = { onEvent(SessionFormUiEvent.ModalDismissed) },
                )
            }
        }
    }
}