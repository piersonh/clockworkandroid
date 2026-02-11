package com.wordco.clockworkandroid.session_report_feature.ui

import android.content.ClipData
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.composables.ErrorReport
import com.wordco.clockworkandroid.core.ui.composables.SpinningLoader
import com.wordco.clockworkandroid.core.ui.theme.ClockWorkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews
import com.wordco.clockworkandroid.core.ui.util.newEntry
import com.wordco.clockworkandroid.session_report_feature.ui.components.DeleteSessionConfirmationModal
import com.wordco.clockworkandroid.session_report_feature.ui.components.SessionReport
import com.wordco.clockworkandroid.session_report_feature.ui.components.SessionReportDropdownMenu
import com.wordco.clockworkandroid.session_report_feature.ui.model.SessionReportModal
import kotlinx.coroutines.launch
import java.time.Duration

@Composable
fun SessionReportPage(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    onEditClick: () -> Unit,
    viewModel: SessionReportViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
                    SessionReportUiEffect.NavigateBack -> {
                        onBackClick()
                    }

                    SessionReportUiEffect.NavigateToContinue -> {
                        onContinueClick()
                    }

                    SessionReportUiEffect.NavigateToEditSession -> {
                        onEditClick()
                    }

                    is SessionReportUiEffect.CopyToClipboard -> {
                        coroutineScope.launch {
                            clipboard.newEntry(
                                label = effect.content,
                                text = effect.content,
                            )
                        }
                    }

                    is SessionReportUiEffect.ShowSnackbar -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = effect.message,
                                //actionLabel = effect.actionLabel,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            }
    }

    SessionReportPageContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionReportPageContent(
    uiState: SessionReportUiState,
    onEvent: (SessionReportUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
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
                    if (uiState !is SessionReportUiState.Deleting) {
                        IconButton(onClick = { onEvent(SessionReportUiEvent.BackClicked) }) {
                            BackImage()
                        }
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                actions = {
                    if (uiState is SessionReportUiState.Retrieved) {
                        Box {
                            IconButton(onClick = { onEvent(SessionReportUiEvent.MenuOpened) }) {
                                Icon(
                                    painterResource(R.drawable.three_dots_vertical),
                                    contentDescription = "More options",
                                    tint = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.padding(vertical = 7.dp)
                                )
                            }

                            SessionReportDropdownMenu(
                                isMenuExpanded = uiState.isMenuOpen,
                                onEvent = onEvent
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            when (uiState) {
                SessionReportUiState.Retrieving -> {
                    SpinningLoader()
                }
                SessionReportUiState.Deleting -> {
                    SpinningLoader()
                }
                is SessionReportUiState.Error -> {
                    Box(
                        modifier = Modifier.padding(top = 40.dp)
                    ) {
                        ErrorReport(
                            uiState.header,
                            uiState.message,
                            onCopyErrorInfoClick = { onEvent(SessionReportUiEvent.CopyErrorClicked) },
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 30.dp)
                        )
                    }
                }
                is SessionReportUiState.Retrieved -> {
                    val scrollState = rememberScrollState()

                    SessionReport(
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
                        currentModal = uiState.currentModal,
                        onEvent = onEvent,
                    )
                }
            }
        }
    }
}


@Composable
private fun ModalManager(
    currentModal: SessionReportModal?,
    onEvent: (SessionReportUiEvent) -> Unit,
) {
    when (currentModal) {
        SessionReportModal.DeleteConfirmation -> {
            DeleteSessionConfirmationModal(
                onDismiss = { onEvent(SessionReportUiEvent.ModalDismissed) },
                onConfirm = { onEvent(SessionReportUiEvent.DeleteConfirmed) }
            )
        }
        null -> {}
    }
}


private class UiStateProvider : PreviewParameterProvider<SessionReportUiState> {
    override val values = sequenceOf(
        SessionReportUiState.Retrieving,
        SessionReportUiState.Error(
            header = "Whoops!",
            message = "You need to put the CD..."
        ),
        SessionReportUiState.Retrieved(
            name = "Preview",
            estimate = Duration.ofHours(1).plusMinutes(27),
            workTime = Duration.ofHours(1).plusMinutes(27),
            breakTime = Duration.ofHours(1).plusMinutes(27),
            totalTime = Duration.ofHours(1).plusMinutes(27),
            totalTimeAccuracy = 0.5,
            isMenuOpen = true,
            currentModal = null,
        ),
        SessionReportUiState.Deleting
    )
}

@AspectRatioPreviews
@Composable
private fun PreviewReportScreen(
    @PreviewParameter(UiStateProvider::class) state: SessionReportUiState
) {
    ClockWorkTheme {
        SessionReportPageContent(
            uiState = state,
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}