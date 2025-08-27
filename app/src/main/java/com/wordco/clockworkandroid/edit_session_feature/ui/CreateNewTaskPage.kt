package com.wordco.clockworkandroid.edit_session_feature.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.core.ui.composables.BackImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.EditTaskForm
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import java.time.LocalDate
import java.time.LocalTime


@Composable
fun CreateNewTaskPage (
    onBackClick: () -> Unit,
    createNewTaskViewModel: CreateNewTaskViewModel
) {
    val uiState by createNewTaskViewModel.uiState.collectAsStateWithLifecycle()

    CreateNewTaskPage(
        uiState = uiState,
        onBackClick = onBackClick,
        onTaskNameChange = createNewTaskViewModel::onTaskNameChange,
        onColorSliderChange = createNewTaskViewModel::onColorSliderChange,
        onDifficultyChange = createNewTaskViewModel::onDifficultyChange,
        onShowDatePicker = createNewTaskViewModel::onShowDatePicker,
        onDismissDatePicker = createNewTaskViewModel::onDismissDatePicker,
        onDueDateChange = createNewTaskViewModel::onDueDateChange,
        onShowTimePicker = createNewTaskViewModel::onShowTimePicker,
        onDismissTimePicker = createNewTaskViewModel::onDismissTimePicker,
        onDueTimeChange = createNewTaskViewModel::onDueTimeChange,
        onEstimateChange = createNewTaskViewModel::onEstimateChange,
        onCreateTaskClick = createNewTaskViewModel::onCreateTaskClick,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewTaskPage(
    uiState: CreateNewTaskUiState,
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
    onCreateTaskClick: () -> CreateNewTaskViewModel.CreateTaskResult,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary, topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ), title = {
                    Row(modifier = Modifier.padding(end = 10.dp)) {
                        IconButton(onClick = onBackClick) {
                            BackImage()
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        EditTaskForm(
            uiState = uiState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 30.dp, vertical = 20.dp),
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
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,

                        ),
                    onClick = {
                        when (onCreateTaskClick()) {
                            CreateNewTaskViewModel.CreateTaskResult.MissingName -> {}
                            CreateNewTaskViewModel.CreateTaskResult.Success -> onBackClick()
                        }
                    }
                ) {
                    Text(
                        "Add",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)

                    )
                }
            }
        )
    }
}

@Preview
@Composable
private fun CreateNewTaskPagePreview() {
    ClockworkTheme {
        CreateNewTaskPage(
            uiState = CreateNewTaskUiState(
                taskName = "",
                colorSliderPos = 0f,
                difficulty = 0f,
                dueDate = LocalDate.parse("2025-12-05"),
                dueTime = LocalTime.parse("10:15"),
                currentModal = null,
                estimate = UserEstimate(15, 2)
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
            onCreateTaskClick = { CreateNewTaskViewModel.CreateTaskResult.Success }
        )
    }
}