package com.wordco.clockworkandroid.edit_profile_feature.ui

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.wordco.clockworkandroid.edit_profile_feature.ui.elements.EditProfileForm
import com.wordco.clockworkandroid.edit_profile_feature.ui.model.Modal

@Composable
fun EditProfilePage(
    viewModel: EditProfileViewModel,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    EditProfilePage(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onNameChange = viewModel::onNameChange,
        onColorSliderChange = viewModel::onColorSliderChange,
        onDifficultyChange = viewModel::onDifficultyChange,
        onSaveClick = viewModel::onSaveClick,
    )

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfilePage(
    uiState: EditProfileUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onColorSliderChange: (Float) -> Unit,
    onDifficultyChange: (Float) -> Unit,
    onSaveClick: () -> Boolean,
) {
    val scrollState = rememberScrollState()

    var currentModal by remember { mutableStateOf<Modal?>(null) }

    val onBackClickCheckChanges = {
        if (uiState is EditProfileUiState.Retrieved && uiState.hasFieldChanges) {
            currentModal = Modal.Discard
        } else {
            onBackClick()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Template",
                        fontFamily = LATO,
                        fontWeight = FontWeight.Black,
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
                            val saveSucceeded = onSaveClick()
                            if (saveSucceeded) {
                                onBackClick()
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
            when (uiState) {
                is EditProfileUiState.Retrieved -> {
                    EditProfileForm(
                        uiState = uiState,
                        modifier = Modifier
                            .padding(
                                horizontal = 30.dp,
                                vertical = 20.dp
                            )
                            .verticalScroll(scrollState),
                        onNameChange = onNameChange,
                        onColorSliderChange = onColorSliderChange,
                        onDifficultyChange = onDifficultyChange,
                    )

                    BackHandler(enabled = uiState.hasFieldChanges) {
                        currentModal = Modal.Discard
                    }

                    if (currentModal == Modal.Discard) {
                        DiscardAlert(
                            onDismiss = { currentModal = null },
                            onConfirm = onBackClick,
                        )
                    }

                }
                EditProfileUiState.Retrieving -> Text("Loading...")
            }
        }
    }
}

@Preview
@Composable
private fun EditProfilePagePreview() {
    ClockworkTheme {
        EditProfilePage(
            uiState = EditProfileUiState.Retrieved(
                name = "Preview",
                colorSliderPos = 0.5f,
                difficulty = 1f,
                hasFieldChanges = false,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onNameChange = {},
            onColorSliderChange = {},
            onDifficultyChange = {},
            onSaveClick = { false },
        )
    }
}