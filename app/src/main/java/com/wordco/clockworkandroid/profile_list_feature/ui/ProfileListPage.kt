package com.wordco.clockworkandroid.profile_list_feature.ui

import android.content.ClipData
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.composables.ErrorReport
import com.wordco.clockworkandroid.core.ui.composables.NavBar
import com.wordco.clockworkandroid.core.ui.composables.PlusImage
import com.wordco.clockworkandroid.core.ui.composables.SpinningLoader
import com.wordco.clockworkandroid.core.ui.theme.ClockWorkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews
import com.wordco.clockworkandroid.core.ui.util.FAKE_TOP_LEVEL_DESTINATIONS
import com.wordco.clockworkandroid.core.ui.util.newEntry
import com.wordco.clockworkandroid.profile_list_feature.ui.components.EmptyProfileList
import com.wordco.clockworkandroid.profile_list_feature.ui.components.ProfileList
import com.wordco.clockworkandroid.profile_list_feature.ui.model.mapper.toProfileListItem
import kotlinx.coroutines.launch

@Composable
fun ProfileListPage(
    viewModel: ProfileListViewModel,
    navBar: @Composable () -> Unit,
    onProfileClick: (Long) -> Unit,
    onCreateNewProfileClick: () -> Unit,
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
                    is ProfileListUiEffect.CopyToClipboard -> {
                        coroutineScope.launch {
                            clipboard.newEntry(
                                label = effect.content,
                                text = effect.content,
                            )
                        }
                    }

                    is ProfileListUiEffect.ShowSnackbar -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = effect.message,
                                //actionLabel = effect.actionLabel,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }

                    ProfileListUiEffect.NavigateToCreateProfile -> {
                        onCreateNewProfileClick()
                    }
                    is ProfileListUiEffect.NavigateToProfile -> {
                        onProfileClick(effect.id)
                    }
                }
            }
    }

    ProfileListPageContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        navBar = navBar,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileListPageContent(
    uiState: ProfileListUiState,
    onEvent: (ProfileListUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    navBar: @Composable () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Task Templates",
                        fontFamily = LATO,
                        fontWeight = FontWeight.Black,
                    )
                },
                actions = {
                    if (uiState is ProfileListUiState.Retrieved) {
                        IconButton(
                            onClick = { onEvent(ProfileListUiEvent.CreateProfileClicked) }
                        ) {
                            PlusImage(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .fillMaxSize()
                            )
                        }
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
            )
        },
        bottomBar = navBar,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->

        Box(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            when (uiState) {
                is ProfileListUiState.Error -> {
                    ErrorReport(
                        header = uiState.header,
                        message = uiState.message,
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 30.dp),
                        onCopyErrorInfoClick = { onEvent(ProfileListUiEvent.CopyErrorClicked) }
                    )
                }
                is ProfileListUiState.Retrieved -> {
                    if (uiState.profiles.isEmpty()) {
                        EmptyProfileList(
                            onCreateProfileClick = { onEvent(ProfileListUiEvent.CreateProfileClicked) }
                        )
                    } else {
                        ProfileList(
                            uiState = uiState,
                            onProfileClick = { id -> onEvent(ProfileListUiEvent.ProfileClicked(id)) }
                        )
                    }
                }
                ProfileListUiState.Retrieving -> {
                    SpinningLoader()
                }
            }
        }
    }
}

private class UiStateProvider : PreviewParameterProvider<ProfileListUiState> {
    override val values = sequenceOf(
        ProfileListUiState.Retrieving,
        ProfileListUiState.Error(
            header = "My head hurt",
            message = "RAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        ),
        ProfileListUiState.Retrieved(
            profiles = DummyData.PROFILES.map { it.toProfileListItem() }
        ),
        ProfileListUiState.Retrieved(
            profiles = emptyList()
        ),
    )
}

@AspectRatioPreviews
@Composable
private fun PreviewReportScreen(
    @PreviewParameter(UiStateProvider::class) state: ProfileListUiState
) {
    ClockWorkTheme {
        ProfileListPageContent(
            uiState = state,
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() },
            navBar = { NavBar(
                items = FAKE_TOP_LEVEL_DESTINATIONS,
                currentDestination = Unit::class,
                navigateTo = {},
            ) }
        )
    }
}