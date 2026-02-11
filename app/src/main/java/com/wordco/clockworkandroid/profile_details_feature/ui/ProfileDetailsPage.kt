package com.wordco.clockworkandroid.profile_details_feature.ui

import android.content.ClipData
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.ui.composables.ErrorReport
import com.wordco.clockworkandroid.core.ui.composables.SpinningLoader
import com.wordco.clockworkandroid.core.ui.theme.ClockWorkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews
import com.wordco.clockworkandroid.core.ui.util.newEntry
import com.wordco.clockworkandroid.profile_details_feature.ui.components.DeleteProfileConfirmationModal
import com.wordco.clockworkandroid.profile_details_feature.ui.components.ProfileDetails
import com.wordco.clockworkandroid.profile_details_feature.ui.components.ProfileDetailsDropdownMenu
import com.wordco.clockworkandroid.profile_details_feature.ui.model.ProfileDetailsModal
import com.wordco.clockworkandroid.profile_details_feature.ui.util.contrastRatioWith
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProfileDetailsPage(
    viewModel: ProfileDetailsViewModel,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onTodoSessionClick: (Long) -> Unit,
    onCreateNewSessionClick: () -> Unit,
    onCompletedSessionClick: (Long) -> Unit,
    navBar: @Composable () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    // see https://stackoverflow.com/questions/79692173/how-to-resolve-deprecated-clipboardmanager-in-jetpack-compose
    val clipboard = LocalClipboard.current


    LaunchedEffect(viewModel.uiEffect, lifecycleOwner) {
        viewModel.uiEffect
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    ProfileDetailsUiEffect.NavigateBack -> {
                        onBackClick()
                    }

                    is ProfileDetailsUiEffect.CopyToClipboard -> {
                        coroutineScope.launch {
                            clipboard.newEntry(
                                label = effect.content,
                                text = effect.content,
                            )
                        }
                    }

                    is ProfileDetailsUiEffect.ShowSnackbar -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = effect.message,
                                //actionLabel = effect.actionLabel,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }

                    is ProfileDetailsUiEffect.NavigateToCompletedSession -> {
                        onCompletedSessionClick(effect.id)
                    }

                    ProfileDetailsUiEffect.NavigateToCreateSession -> {
                        onCreateNewSessionClick()
                    }

                    is ProfileDetailsUiEffect.NavigateToTodoSession -> {
                        onTodoSessionClick(effect.id)
                    }

                    ProfileDetailsUiEffect.NavigateToProfileEditor -> {
                        onEditClick()
                    }
                }
            }
    }

    ProfileDetailsPageContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        navBar = navBar,
        coroutineScope = coroutineScope,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileDetailsPageContent(
    uiState: ProfileDetailsUiState,
    onEvent: (ProfileDetailsUiEvent) -> Unit,
    navBar: @Composable () -> Unit,
    coroutineScope: CoroutineScope,
) {

    val accentColor = if (uiState is ProfileDetailsUiState.Retrieved) {
        uiState.profileColor
    } else {
        MaterialTheme.colorScheme.secondary
    }

    val onAccentColor = if (uiState is ProfileDetailsUiState.Retrieved) {
        listOf(
            Color.White,
            Color.Black,
        ).maxBy {
            uiState.profileColor.contrastRatioWith(it)
        }
    } else {
        MaterialTheme.colorScheme.onSecondary
    }

    Scaffold (
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (uiState !is ProfileDetailsUiState.Deleting) {
                        IconButton(
                            onClick = { onEvent(ProfileDetailsUiEvent.BackClicked) },
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = "Back",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.aspectRatio(0.7f),
                                colorFilter = ColorFilter.tint(color = onAccentColor)
                            )
                        }
                    }
                },
                colors = topAppBarColors(
                    containerColor = accentColor,
                    titleContentColor = onAccentColor
                ),
                title = {
                    Text(
                        "Profile Details",
                        fontFamily = LATO,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                actions = {
                    if (uiState is ProfileDetailsUiState.Retrieved) {
                        Box {
                            IconButton(onClick = { onEvent(ProfileDetailsUiEvent.MenuOpened) }) {
                                Icon(
                                    painterResource(R.drawable.three_dots_vertical),
                                    contentDescription = "More options",
                                    tint = onAccentColor,
                                    modifier = Modifier.padding(vertical = 7.dp)
                                )
                            }

                            ProfileDetailsDropdownMenu(
                                isMenuExpanded = uiState.isMenuOpen,
                                onEvent = onEvent,
                            )
                        }
                    }
                }
            )
        },
        bottomBar = navBar,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when(uiState) {
                ProfileDetailsUiState.Retrieving -> {
                    SpinningLoader()
                }
                ProfileDetailsUiState.Deleting -> {
                    SpinningLoader()
                }
                is ProfileDetailsUiState.Error -> {
                    Box(
                        modifier = Modifier.padding(top = 40.dp).padding(horizontal = 5.dp)
                    ) {
                        ErrorReport(
                            uiState.header,
                            uiState.message,
                            onCopyErrorInfoClick = { onEvent(ProfileDetailsUiEvent.CopyErrorClicked) },
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 30.dp)
                        )
                    }
                }
                is ProfileDetailsUiState.Retrieved -> {
                    ProfileDetails(
                        uiState = uiState,
                        onEvent = onEvent,
                        coroutineScope = coroutineScope,
                        accentColor = accentColor,
                        onAccentColor = onAccentColor,
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
    currentModal: ProfileDetailsModal?,
    onEvent: (ProfileDetailsUiEvent) -> Unit,
) {
    when (currentModal) {
        ProfileDetailsModal.DeleteConfirmation -> {
            DeleteProfileConfirmationModal(
                onDismiss = { onEvent(ProfileDetailsUiEvent.ModalDismissed) },
                onConfirm = { onEvent(ProfileDetailsUiEvent.DeleteConfirmed) }
            )
        }
        null -> {}
    }
}

private class UiStateProvider : PreviewParameterProvider<ProfileDetailsUiState> {
    override val values = sequenceOf(
        ProfileDetailsUiState.Retrieving,
        ProfileDetailsUiState.Error(
            header = "Encountered a Fatal Error!",
            message = "ClockWork encountered an error and could not recover..."
        ),
        ProfileDetailsUiState.Retrieved(
            profileName = "Preview",
            profileColor = Color.Yellow,
            todoSessions = emptyList(),
            completeSessions = emptyList(),
            isMenuOpen = false,
            currentModal = null,
        ),
        ProfileDetailsUiState.Deleting
    )
}

@PreviewLightDark
@AspectRatioPreviews
@Composable
private fun PreviewReportScreen(
    @PreviewParameter(UiStateProvider::class) state: ProfileDetailsUiState
) {
    ClockWorkTheme {
        ProfileDetailsPageContent(
            uiState = state,
            onEvent = {},
            navBar = {},
            coroutineScope = rememberCoroutineScope(),
        )
    }
}