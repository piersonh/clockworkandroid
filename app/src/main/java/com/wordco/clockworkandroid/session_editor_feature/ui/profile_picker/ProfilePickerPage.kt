package com.wordco.clockworkandroid.session_editor_feature.ui.profile_picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.composables.ErrorReport
import com.wordco.clockworkandroid.core.ui.composables.PlusImage
import com.wordco.clockworkandroid.core.ui.composables.SpinningLoader
import com.wordco.clockworkandroid.core.ui.theme.ClockWorkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews
import com.wordco.clockworkandroid.core.ui.util.newEntry
import com.wordco.clockworkandroid.session_editor_feature.ui.profile_picker.components.ProfilePicker
import com.wordco.clockworkandroid.session_editor_feature.ui.profile_picker.model.mapper.toProfilePickerItem
import kotlinx.coroutines.launch

@Composable
fun ProfilePickerPage(
    viewModel: ProfilePickerViewModel,
    onBackClick: () -> Unit,
    onProfileSelected: (Long?) -> Unit,
    onNavigateToCreateProfile: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    // see https://stackoverflow.com/questions/79692173/how-to-resolve-deprecated-clipboardmanager-in-jetpack-compose
    val clipboard = LocalClipboard.current

    LaunchedEffect(viewModel.uiEffect, lifecycleOwner) {
        viewModel.uiEffect
            .flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { effect ->
                when (effect) {
                    is ProfilePickerUiEffect.NavigateBack -> onBackClick()

                    is ProfilePickerUiEffect.FinishWithResult -> {
                        onProfileSelected(effect.profileId)
                    }

                    ProfilePickerUiEffect.NavigateToCreateProfile -> onNavigateToCreateProfile()
                    is ProfilePickerUiEffect.CopyToClipboard -> {
                        coroutineScope.launch {
                            clipboard.newEntry(
                                label = effect.content,
                                text = effect.content,
                            )
                        }
                    }
                    is ProfilePickerUiEffect.ShowSnackbar -> {
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

    ProfilePickerPageContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfilePickerPageContent(
    uiState: ProfilePickerUiState,
    onEvent: (ProfilePickerUiEvent) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Select a Template",
                        fontFamily = LATO,
                        fontWeight = FontWeight.Black,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onEvent(ProfilePickerUiEvent.BackClicked)
                        }
                    ) {
                        BackImage()
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                actions = {
                    IconButton(
                        onClick = { onEvent(ProfilePickerUiEvent.CreateProfileClicked) }
                    ) {
                        PlusImage(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxSize()
                        )
                    }
                }
            )
        },
    ) { paddingValues ->

        Box(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            when(uiState) {
                is ProfilePickerUiState.Error -> {
                    Box(
                        modifier = Modifier.padding(top = 40.dp)
                    ) {
                        ErrorReport(
                            uiState.header,
                            uiState.message,
                            onCopyErrorInfoClick = { onEvent(ProfilePickerUiEvent.CopyErrorClicked) },
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 30.dp)
                        )
                    }
                }
                is ProfilePickerUiState.Retrieved -> {
                    ProfilePicker(
                        profiles = uiState.profiles,
                        modifier = Modifier,
                        onProfileClick = { onEvent(ProfilePickerUiEvent.ProfileClicked(it)) },
                        onCreateProfileClick = { onEvent(ProfilePickerUiEvent.CreateProfileClicked) }
                    )
                }
                ProfilePickerUiState.Retrieving -> {
                    SpinningLoader()
                }
            }
        }
    }
}


private class PickerStateProvider : PreviewParameterProvider<ProfilePickerUiState> {
    override val values = sequenceOf(
        ProfilePickerUiState.Retrieving,
        ProfilePickerUiState.Error(
            header = "Encountered an Error",
            message = "Something happened... Idk I wasn't watching"
        ),
        ProfilePickerUiState.Retrieved(
            profiles = DummyData.PROFILES.map { it.toProfilePickerItem() },
            selectedProfileId = 0
        )
    )
}

@AspectRatioPreviews
@Composable
private fun PreviewFormScreen(
    @PreviewParameter(PickerStateProvider::class) state: ProfilePickerUiState
) {
    ClockWorkTheme {
        ProfilePickerPageContent(
            uiState = state,
            onEvent = {}
        )
    }
}