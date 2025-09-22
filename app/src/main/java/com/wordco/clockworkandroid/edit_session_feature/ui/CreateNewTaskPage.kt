package com.wordco.clockworkandroid.edit_session_feature.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CreateNewTaskPage (
    onBackClick: () -> Unit,
    viewModel: CreateNewTaskViewModel,
    skipProfilePicker: Boolean = false,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditTaskPage(
        uiState = uiState,
        onBackClick = onBackClick,
        title = "Create New Session",
        initialPage = if (skipProfilePicker) 1 else 0,
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
        onShowEstimatePicker = viewModel::onShowEstimatePicker,
        onDismissEstimatePicker = viewModel::onDismissEstimatePicker,
        onSaveClick = viewModel::onSaveClick,
    )
}