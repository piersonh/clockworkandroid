package com.wordco.clockworkandroid.edit_session_feature.ui

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
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.EditTaskForm
import com.wordco.clockworkandroid.edit_session_feature.ui.model.EditTaskResult
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toProfilePickerItem
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun EditTaskPage (
    onBackClick: () -> Unit,
    editTaskViewModel: EditTaskViewModel
) {
    val uiState by editTaskViewModel.uiState.collectAsStateWithLifecycle()

    EditTaskPage(
        uiState = uiState,
        onBackClick = onBackClick,
        onTaskNameChange = editTaskViewModel::onTaskNameChange,
        onColorSliderChange = editTaskViewModel::onColorSliderChange,
        onDifficultyChange = editTaskViewModel::onDifficultyChange,
        onShowDatePicker = editTaskViewModel::onShowDatePicker,
        onDismissDatePicker = editTaskViewModel::onDismissDatePicker,
        onDueDateChange = editTaskViewModel::onDueDateChange,
        onShowTimePicker = editTaskViewModel::onShowTimePicker,
        onDismissTimePicker = editTaskViewModel::onDismissTimePicker,
        onDueTimeChange = editTaskViewModel::onDueTimeChange,
        onEstimateChange = editTaskViewModel::onEstimateChange,
        onEditTaskClick = editTaskViewModel::onEditTaskClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTaskPage(
    uiState: EditTaskUiState,
    onBackClick: () -> Unit,
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
    onEditTaskClick: () -> EditTaskResult,
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
                        "Edit Session",
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
                            when (onEditTaskClick()) {
                                EditTaskResult.MissingName -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Failed to save session: Missing Name"
                                        )
                                    }
                                }
                                EditTaskResult.Success -> onBackClick()
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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        when (uiState) {
            EditTaskUiState.Retrieving -> Text("Loading...")
            is EditTaskUiState.Retrieved -> EditTaskForm(
                uiState = uiState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(horizontal = 30.dp, vertical = 20.dp)
                    .verticalScroll(scrollState),
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
                onShowProfilePicker = {},
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
            onBackClick = { },
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
            onEditTaskClick = { EditTaskResult.Success }
        )
    }
}