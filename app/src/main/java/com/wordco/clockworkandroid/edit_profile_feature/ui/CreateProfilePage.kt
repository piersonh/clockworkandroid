package com.wordco.clockworkandroid.edit_profile_feature.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.edit_profile_feature.ui.elements.EditProfileForm
import com.wordco.clockworkandroid.edit_profile_feature.ui.model.CreateProfileResult
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
        onCreateProfileClick = viewModel::onCreateProfileClick,
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
    onCreateProfileClick: () -> CreateProfileResult,
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


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
                    IconButton(onClick = onBackClick) {
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
                    TextButton(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        onClick = {
                            when (onCreateProfileClick()) {
                                CreateProfileResult.MissingName -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Failed to save profile: Missing Name"
                                        )
                                    }
                                }
                                CreateProfileResult.Success -> onBackClick()
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            "Add",
                            fontFamily = LATO,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp,
                            )
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
            ),
            onBackClick = {},
            onNameChange = {},
            onColorSliderChange = {},
            onDifficultyChange = {},
            onCreateProfileClick = { CreateProfileResult.Success},
        )
    }
}