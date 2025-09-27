package com.wordco.clockworkandroid.edit_profile_feature.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.core.ui.composables.AccentRectangleTextButton
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.composables.DiscardAlert
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.Fallible
import com.wordco.clockworkandroid.edit_profile_feature.ui.elements.EditProfileForm
import com.wordco.clockworkandroid.edit_profile_feature.ui.model.Modal
import com.wordco.clockworkandroid.edit_profile_feature.ui.model.SaveProfileError
import kotlinx.coroutines.launch

@Composable
fun CreateProfilePage(
    viewModel: CreateProfileViewModel,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateProfilePage(
        uiState = uiState,
        onBackClick = onBackClick,
        onNameChange = viewModel::onNameChange,
        onColorSliderChange = viewModel::onColorSliderChange,
        onDifficultyChange = viewModel::onDifficultyChange,
        onShowDiscardAlert = viewModel::onShowDiscardAlert,
        onDismissModal = viewModel::onDismissModal,
        onCreateProfileClick = viewModel::onSaveClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateProfilePage(
    uiState: CreateProfileUiState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onColorSliderChange: (Float) -> Unit,
    onDifficultyChange: (Float) -> Unit,
    onShowDiscardAlert: () -> Unit,
    onDismissModal: () -> Unit,
    onCreateProfileClick: () -> Fallible<SaveProfileError>,
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val onBackClickCheckChanges = {
        if (uiState.hasFieldChanges) {
            onShowDiscardAlert()
        } else {
            onBackClick()
        }
    }

    BackHandler(enabled = uiState.hasFieldChanges) {
        Log.i("BackHandler", "triggered")
        onShowDiscardAlert()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Create Profile",
                        fontFamily = LATO,
                    )

                },
                navigationIcon = {
                    IconButton(onClick = onBackClickCheckChanges) {
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
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    AccentRectangleTextButton(
                        onClick = {
                            when (onCreateProfileClick().takeIfError()) {
                                SaveProfileError.MISSING_NAME -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Failed to save profile: Missing Name"
                                        )
                                    }
                                }
                                null -> onBackClick()
                            }
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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            EditProfileForm(
                uiState = uiState,
                modifier = Modifier
                    .padding(
                        horizontal = 30.dp,
                        vertical = 20.dp
                    )
                    .verticalScroll(scrollState)
                ,
                onNameChange = onNameChange,
                onColorSliderChange = onColorSliderChange,
                onDifficultyChange = onDifficultyChange,
                confirmButton = { }
            )
        }

        if (uiState.currentModal == Modal.Discard) {
            DiscardAlert(
                onDismiss = onDismissModal,
                onConfirm = onBackClick
            )
        }
    }
}

@Preview
@Composable
private fun CreateProfilePagePreview() {
    ClockworkTheme {
        CreateProfilePage(
            uiState = CreateProfileUiState(
                name = "Preview",
                colorSliderPos = 0.5f,
                difficulty = 1f,
                currentModal = null,
                hasFieldChanges = false,
            ),
            onBackClick = {},
            onNameChange = {},
            onColorSliderChange = {},
            onDifficultyChange = {},
            onShowDiscardAlert = {},
            onDismissModal = {},
            onCreateProfileClick = { Fallible.Success }
        )
    }
}