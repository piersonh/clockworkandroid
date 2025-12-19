package com.wordco.clockworkandroid.edit_profile_feature.ui

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.wordco.clockworkandroid.core.ui.composables.AccentRectangleTextButton
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.composables.DiscardAlert
import com.wordco.clockworkandroid.core.ui.composables.SpinningLoader
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.edit_profile_feature.ui.elements.ProfileForm
import com.wordco.clockworkandroid.edit_profile_feature.ui.model.ProfileFormModal
import kotlinx.coroutines.launch

@Composable
fun ProfileFormPage(
    viewModel: ProfileFormViewModel,
    onBackClick: () -> Unit,
) {

    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.uiEffect, lifecycleOwner) {
        // flowWithLifecycle ensures collection stops when app is in background
        viewModel.uiEffect
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    ProfileFormUiEffect.NavigateBack -> onBackClick()
                    is ProfileFormUiEffect.ShowSnackbar -> {
                        launch {
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

    ProfileFormPageContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileFormPageContent(
    state: ProfileFormUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (ProfileFormUiEvent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.title,
                        fontFamily = LATO,
                        fontWeight = FontWeight.Black,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onEvent(ProfileFormUiEvent.BackClicked)
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
            if (state is ProfileFormUiState.Retrieved) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        AccentRectangleTextButton(
                            onClick = {
                                onEvent(ProfileFormUiEvent.SaveClicked)
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
            when (val currentState = state) {
                is ProfileFormUiState.Retrieving -> {
                    SpinningLoader()
                }
                is ProfileFormUiState.Retrieved -> {
                    BackHandler(enabled = currentState.hasFormChanges) {
                        onEvent(ProfileFormUiEvent.BackClicked)
                    }

                    val scrollState = rememberScrollState()

                    ProfileForm(
                        uiState = currentState,
                        modifier = Modifier
                            .padding(
                                horizontal = 30.dp,
                                vertical = 20.dp
                            )
                            .verticalScroll(scrollState),
                        onEvent = onEvent,
                    )


                    ModalManager(
                        currentModal = currentState.currentModal,
                        onEvent = onEvent,
                    )
                }
            }
        }
    }
}


@Composable
private fun ModalManager(
    currentModal: ProfileFormModal?,
    onEvent: (ProfileFormUiEvent) -> Unit,
) {
    when (currentModal) {
        null -> {}
        ProfileFormModal.Discard -> {
            DiscardAlert(
                onDismiss = { onEvent(ProfileFormUiEvent.ModalDismissed) },
                onConfirm = { onEvent(ProfileFormUiEvent.DiscardConfirmed) },
            )
        }
    }
}


class FormStateProvider : PreviewParameterProvider<ProfileFormUiState> {
    override val values = sequenceOf(
        ProfileFormUiState.Retrieving("Preview"),
        ProfileFormUiState.Retrieved(
            title = "Preview",
            name = "Preview",
            colorSliderPos = 0.5f,
            difficulty = 3f,
            hasFormChanges = true,
            currentModal = null,
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewFormScreen(
    @PreviewParameter(FormStateProvider::class) state: ProfileFormUiState
) {
    ClockworkTheme {
        ProfileFormPageContent(
            state = state,
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {}
        )
    }
}