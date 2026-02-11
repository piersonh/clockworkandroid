package com.wordco.clockworkandroid.profile_editor_feature.ui

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
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.wordco.clockworkandroid.core.ui.composables.AccentRectangleTextButton
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.composables.DiscardAlert
import com.wordco.clockworkandroid.core.ui.composables.ErrorReport
import com.wordco.clockworkandroid.core.ui.composables.SpinningLoader
import com.wordco.clockworkandroid.core.ui.theme.ClockWorkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews
import com.wordco.clockworkandroid.core.ui.util.newEntry
import com.wordco.clockworkandroid.profile_editor_feature.ui.components.ProfileForm
import com.wordco.clockworkandroid.profile_editor_feature.ui.model.ProfileEditorModal
import kotlinx.coroutines.launch

@Composable
fun ProfileEditorPage(
    viewModel: ProfileEditorViewModel,
    onBackClick: () -> Unit,
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
                    ProfileEditorUiEffect.NavigateBack -> onBackClick()
                    is ProfileEditorUiEffect.ShowSnackbar -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = effect.message,
                                //actionLabel = effect.actionLabel,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }

                    is ProfileEditorUiEffect.CopyToClipboard -> {
                        coroutineScope.launch {
                            clipboard.newEntry(
                                label = effect.content,
                                text = effect.content,
                            )
                        }
                    }
                }
            }
    }

    ProfileEditorPageContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileEditorPageContent(
    uiState: ProfileEditorUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (ProfileEditorUiEvent) -> Unit,
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
                            onEvent(ProfileEditorUiEvent.BackClicked)
                        }
                    ) {
                        BackImage()
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
            )
        },
        bottomBar = {
            if (uiState is ProfileEditorUiState.Retrieved) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        AccentRectangleTextButton(
                            onClick = {
                                onEvent(ProfileEditorUiEvent.SaveClicked)
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
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            when (uiState) {
                is ProfileEditorUiState.Retrieving -> {
                    SpinningLoader()
                }
                is ProfileEditorUiState.Retrieved -> {
                    BackHandler(enabled = uiState.hasFormChanges) {
                        onEvent(ProfileEditorUiEvent.BackClicked)
                    }

                    val scrollState = rememberScrollState()

                    ProfileForm(
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

                is ProfileEditorUiState.Error -> {
                    Box(
                        modifier = Modifier.padding(top = 40.dp)
                    ) {
                        ErrorReport(
                            uiState.header,
                            uiState.message,
                            onCopyErrorInfoClick = { onEvent(ProfileEditorUiEvent.CopyErrorClicked) },
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 30.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun ModalManager(
    currentModal: ProfileEditorModal?,
    onEvent: (ProfileEditorUiEvent) -> Unit,
) {
    when (currentModal) {
        null -> {}
        ProfileEditorModal.Discard -> {
            DiscardAlert(
                onDismiss = { onEvent(ProfileEditorUiEvent.ModalDismissed) },
                onConfirm = { onEvent(ProfileEditorUiEvent.DiscardConfirmed) },
            )
        }
    }
}


private class FormStateProvider : PreviewParameterProvider<ProfileEditorUiState> {
    override val values = sequenceOf(
        ProfileEditorUiState.Retrieving("Preview"),
        ProfileEditorUiState.Retrieved(
            title = "Preview",
            name = "Preview",
            colorSliderPos = 0.5f,
            difficulty = 3f,
            hasFormChanges = true,
            currentModal = null,
        ),
        ProfileEditorUiState.Error(
            title = "Preview",
            header = "Failed to load",
            message = "message here",
        )
    )
}

@AspectRatioPreviews
@Composable
private fun PreviewEditorScreen(
    @PreviewParameter(FormStateProvider::class) state: ProfileEditorUiState
) {
    ClockWorkTheme {
        ProfileEditorPageContent(
            uiState = state,
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {}
        )
    }
}