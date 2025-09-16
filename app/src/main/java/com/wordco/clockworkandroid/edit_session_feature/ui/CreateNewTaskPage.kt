package com.wordco.clockworkandroid.edit_session_feature.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.EditTaskForm
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.ProfilePicker
import com.wordco.clockworkandroid.edit_session_feature.ui.model.CreateTaskResult
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toProfilePickerItem
import com.wordco.clockworkandroid.edit_session_feature.ui.util.tweenToPage
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime


@Composable
fun CreateNewTaskPage (
    onBackClick: () -> Unit,
    viewModel: CreateNewTaskViewModel,
    skipProfilePicker: Boolean = false,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateNewTaskPage(
        uiState = uiState,
        onBackClick = onBackClick,
        skipProfilePicker = skipProfilePicker,
        onProfileChange = viewModel::onProfileChange,
        onTaskNameChange = viewModel::onTaskNameChange,
        onColorSliderChange = viewModel::onColorSliderChange,
        onDifficultyChange = viewModel::onDifficultyChange,
        onShowDatePicker = viewModel::onShowDatePicker,
        onDismissDatePicker = viewModel::onDismissDatePicker,
        onDueDateChange = viewModel::onDueDateChange,
        onShowTimePicker = viewModel::onShowTimePicker,
        onDismissTimePicker = viewModel::onDismissTimePicker,
        onDueTimeChange = viewModel::onDueTimeChange,
        onEstimateChange = viewModel::onEstimateChange,
        onCreateTaskClick = viewModel::onCreateTaskClick,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewTaskPage(
    uiState: EditTaskUiState,
    skipProfilePicker: Boolean,
    onBackClick: () -> Unit,
    onProfileChange: (Long?) -> Unit,
    onTaskNameChange: (String) -> Unit,
    onColorSliderChange: (Float) -> Unit,
    onDifficultyChange: (Float) -> Unit,
    onShowDatePicker: () -> Unit,
    onDismissDatePicker: () -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onShowTimePicker: () -> Unit,
    onDismissTimePicker: () -> Unit,
    onDueTimeChange: (LocalTime) -> Unit,
    onEstimateChange: (UserEstimate) -> Unit,
    onCreateTaskClick: () -> CreateTaskResult,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Create New Session",
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->

        when (uiState) {
            is EditTaskUiState.Retrieved -> CreateNewTaskPageRetrieved(
                uiState = uiState,
                innerPadding = innerPadding,
                snackbarHostState = snackbarHostState,
                onBackClick = onBackClick,
                skipProfilePicker = skipProfilePicker,
                onProfileChange = onProfileChange,
                onTaskNameChange = onTaskNameChange,
                onColorSliderChange = onColorSliderChange,
                onDifficultyChange = onDifficultyChange,
                onShowDatePicker = onShowDatePicker,
                onDismissDatePicker = onDismissDatePicker,
                onDueDateChange = onDueDateChange,
                onShowTimePicker = onShowTimePicker,
                onDismissTimePicker = onDismissTimePicker,
                onDueTimeChange = onDueTimeChange,
                onEstimateChange = onEstimateChange,
                onCreateTaskClick = onCreateTaskClick
            )
            EditTaskUiState.Retrieving -> Box (
                modifier = Modifier.padding(innerPadding)
            ) {
                Text("Loading...")
            }
        }

    }
}


@Composable
private fun CreateNewTaskPageRetrieved(
    uiState: EditTaskUiState.Retrieved,
    innerPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    skipProfilePicker: Boolean,
    onProfileChange: (Long?) -> Unit,
    onTaskNameChange: (String) -> Unit,
    onColorSliderChange: (Float) -> Unit,
    onDifficultyChange: (Float) -> Unit,
    onShowDatePicker: () -> Unit,
    onDismissDatePicker: () -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onShowTimePicker: () -> Unit,
    onDismissTimePicker: () -> Unit,
    onDueTimeChange: (LocalTime) -> Unit,
    onEstimateChange: (UserEstimate) -> Unit,
    onCreateTaskClick: () -> CreateTaskResult,
) {
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(
        initialPage = if (skipProfilePicker || uiState.profiles.isEmpty()) 1 else 0,
        pageCount = { 2 }
    )
    val coroutineScope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false,
        verticalAlignment = Alignment.Top,
    ) { page ->
        when (page) {
            1 -> Column {
                Box(
                    modifier = Modifier.padding(innerPadding),
                ) {
                    EditTaskForm(
                        uiState = uiState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 20.dp)
                            .verticalScroll(scrollState),
                        onShowProfilePicker = {
                            coroutineScope.launch {
                                pagerState.tweenToPage(0)
                            }
                        },
                        onTaskNameChange = onTaskNameChange,
                        onColorSliderChange = onColorSliderChange,
                        onDifficultyChange = onDifficultyChange,
                        onShowDatePicker = onShowDatePicker,
                        onDismissDatePicker = onDismissDatePicker,
                        onDueDateChange = onDueDateChange,
                        onShowTimePicker = onShowTimePicker,
                        onDismissTimePicker = onDismissTimePicker,
                        onDueTimeChange = onDueTimeChange,
                        onEstimateChange = onEstimateChange,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

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
                                when (onCreateTaskClick()) {
                                    CreateTaskResult.MissingName -> {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                "Failed to save session: Missing Name"
                                            )
                                        }
                                    }
                                    CreateTaskResult.Success -> onBackClick()
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
            }
            0 -> ProfilePicker(
                profiles = uiState.profiles,
                modifier = Modifier.padding(innerPadding),
                onProfileClick = { profileId ->
                    onProfileChange(profileId)
                    coroutineScope.launch {
                        pagerState.tweenToPage(1)
                    }
                }
            )
        }
    }
}


@Preview
@Composable
private fun CreateNewTaskPagePreview() {
    ClockworkTheme {
        CreateNewTaskPage(
            uiState = EditTaskUiState.Retrieved(
                taskName = "",
                colorSliderPos = 0f,
                difficulty = 0f,
                dueDate = LocalDate.parse("2025-12-05"),
                dueTime = LocalTime.parse("10:15"),
                currentModal = null,
                estimate = UserEstimate(15, 2),
                profileName = "Preview",
                profiles = DummyData.PROFILES.map { it.toProfilePickerItem() },
            ),
            onBackClick = { },
            skipProfilePicker = false,
            onProfileChange = { },
            onTaskNameChange = { },
            onColorSliderChange = { },
            onDifficultyChange = { },
            onShowDatePicker = { },
            onDismissDatePicker = { },
            onDueDateChange = { },
            onShowTimePicker = { },
            onDismissTimePicker = { },
            onDueTimeChange = { },
            onEstimateChange = { },
            onCreateTaskClick = { CreateTaskResult.Success }
        )
    }
}