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
import com.wordco.clockworkandroid.core.ui.util.Fallible
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.EditTaskForm
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.ProfilePicker
import com.wordco.clockworkandroid.edit_session_feature.ui.model.SaveSessionError
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toProfilePickerItem
import com.wordco.clockworkandroid.edit_session_feature.ui.util.tweenToPage
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun EditTaskPage (
    onBackClick: () -> Unit,
    viewModel: EditTaskViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditTaskPage(
        uiState = uiState,
        onBackClick = onBackClick,
        onTaskNameChange = viewModel::onTaskNameChange,
        onProfileChange = viewModel::onProfileChange,
        onColorSliderChange = viewModel::onColorSliderChange,
        onDifficultyChange = viewModel::onDifficultyChange,
        onShowDatePicker = viewModel::onShowDatePicker,
        onDismissDatePicker = viewModel::onDismissDatePicker,
        onDueDateChange = viewModel::onDueDateChange,
        onShowTimePicker = viewModel::onShowTimePicker,
        onDismissTimePicker = viewModel::onDismissTimePicker,
        onDueTimeChange = viewModel::onDueTimeChange,
        onEstimateChange = viewModel::onEstimateChange,
        onSaveClick = viewModel::onEditTaskClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditTaskPage(
    uiState: EditTaskUiState,
    onBackClick: () -> Unit,
    title: String = "Edit Session",
    initialPage: Int = 1,
    onTaskNameChange: (String) -> Unit,
    onProfileChange: (Long?) -> Unit,
    onColorSliderChange: (Float) -> Unit,
    onDifficultyChange: (Float) -> Unit,
    onShowDatePicker: () -> Unit,
    onDismissDatePicker: () -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onShowTimePicker: () -> Unit,
    onDismissTimePicker: () -> Unit,
    onDueTimeChange: (LocalTime) -> Unit,
    onEstimateChange: (UserEstimate) -> Unit,
    onSaveClick: () -> Fallible<SaveSessionError>,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        title,
                        fontFamily = LATO
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
            EditTaskUiState.Retrieving -> Box (
                modifier = Modifier.padding(innerPadding)
            ) {
                Text("Loading...")
            }
            is EditTaskUiState.Retrieved -> EditSessionPageRetrieved(
                uiState = uiState,
                innerPadding = innerPadding,
                snackbarHostState = snackbarHostState,
                onBackClick = onBackClick,
                initialPage = initialPage,
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
                onSaveClick = onSaveClick,
            )
        }
    }
}



@Composable
private fun EditSessionPageRetrieved(
    uiState: EditTaskUiState.Retrieved,
    innerPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onProfileChange: (Long?) -> Unit,
    initialPage: Int,
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
    onSaveClick: () -> Fallible<SaveSessionError>,
) {
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(
        initialPage = if (uiState.profiles.isEmpty()) 1 else initialPage,
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
                                when (onSaveClick().takeIfError()) {
                                    SaveSessionError.MISSING_NAME -> {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                "Failed to save session: Missing Name"
                                            )
                                        }
                                    }
                                    null -> onBackClick()
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "Save",
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
private fun EditTaskPagePreview() {
    ClockworkTheme {
        EditTaskPage(
            uiState = EditTaskUiState.Retrieved(
                taskName = "",
                profileName = "Preview",
                colorSliderPos = 0f,
                difficulty = 0f,
                dueDate = LocalDate.parse("2025-12-05"),
                dueTime = LocalTime.parse("10:15"),
                currentModal = null,
                estimate = UserEstimate(15, 2),
                profiles = DummyData.PROFILES.map{ it.toProfilePickerItem() }
            ),
            initialPage = 0,
            onBackClick = { },
            onTaskNameChange = { },
            onProfileChange = { },
            onColorSliderChange = { },
            onDifficultyChange = { },
            onShowDatePicker = { },
            onDismissDatePicker = { },
            onDueDateChange = { },
            onShowTimePicker = { },
            onDismissTimePicker = { },
            onDueTimeChange = { },
            onEstimateChange = { },
            onSaveClick = { Fallible.Success }
        )
    }
}