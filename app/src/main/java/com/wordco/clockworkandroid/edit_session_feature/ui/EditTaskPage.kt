package com.wordco.clockworkandroid.edit_session_feature.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.ui.composables.AccentRectangleTextButton
import com.wordco.clockworkandroid.core.ui.composables.DiscardAlert
import com.wordco.clockworkandroid.core.ui.composables.PlusImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.DatePickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.EditPageScaffold
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.EstimatePickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.PagerAwareSlideAwayBottomBar
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.ProfilePicker
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.SessionForm
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.TimerPickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.rememberEstimatePickerState
import com.wordco.clockworkandroid.edit_session_feature.ui.model.Modal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toProfilePickerItem
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun EditTaskPage (
    onBackClick: () -> Unit,
    onCreateProfileClick: () -> Unit,
    viewModel: EditTaskViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    EditTaskPage(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onTaskNameChange = viewModel::onTaskNameChange,
        onProfileChange = viewModel::onProfileChange,
        onColorSliderChange = viewModel::onColorSliderChange,
        onDifficultyChange = viewModel::onDifficultyChange,
        onDueDateChange = viewModel::onDueDateChange,
        onDueTimeChange = viewModel::onDueTimeChange,
        onEstimateChange = viewModel::onEstimateChange,
        onCreateNewProfileClick = onCreateProfileClick,
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
private fun EditTaskPage(
    uiState: EditTaskUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onTaskNameChange: (String) -> Unit,
    onProfileChange: (Long?) -> Unit,
    onColorSliderChange: (Float) -> Unit,
    onDifficultyChange: (Float) -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onDueTimeChange: (LocalTime) -> Unit,
    onEstimateChange: (UserEstimate?) -> Unit,
    onCreateNewProfileClick: () -> Unit,
    onSaveClick: () -> Boolean,
) {

    when (uiState) {
        EditTaskUiState.Retrieving -> EditPageScaffold(
            title = "Edit Session",
            onBackClick = onBackClick,
            snackbarHostState = snackbarHostState,
            content = { paddingValues ->
                Box(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    Text("Loading...")
                }
            },
        )

        is EditTaskUiState.Retrieved -> EditSessionPageRetrieved(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
            onProfileChange = onProfileChange,
            onTaskNameChange = onTaskNameChange,
            onColorSliderChange = onColorSliderChange,
            onDifficultyChange = onDifficultyChange,
            onDueDateChange = onDueDateChange,
            onDueTimeChange = onDueTimeChange,
            onEstimateChange = onEstimateChange,
            onCreateNewProfileClick = onCreateNewProfileClick,
            onSaveClick = onSaveClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSessionPageRetrieved(
    uiState: EditTaskUiState.Retrieved,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onProfileChange: (Long?) -> Unit,
    onTaskNameChange: (String) -> Unit,
    onColorSliderChange: (Float) -> Unit,
    onDifficultyChange: (Float) -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onDueTimeChange: (LocalTime) -> Unit,
    onEstimateChange: (UserEstimate?) -> Unit,
    onCreateNewProfileClick: () -> Unit,
    onSaveClick: () -> Boolean,
) {
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { 2 }
    )
    val coroutineScope = rememberCoroutineScope()

    val dueDatePickerState = rememberDatePickerState()
    val dueTimePickerState = rememberTimePickerState(
        initialHour = uiState.dueTime?.hour ?: 0,
        initialMinute = uiState.dueTime?.minute ?: 0
    )
    val estimatePickerState = rememberEstimatePickerState(
        initialValue = uiState.estimate ?: UserEstimate(0,0)
    )

    var currentModal by remember { mutableStateOf<Modal?>(null) }

    val onBackClickCheckChanges = {
        if (uiState.hasFieldChanges) {
            currentModal = Modal.Discard
        } else {
            onBackClick()
        }
    }

    BackHandler(enabled = uiState.hasFieldChanges) {
        currentModal = Modal.Discard
    }

    EditPageScaffold(
        title = "Edit Session",
        onBackClick = onBackClickCheckChanges,
        topBarActions = {
            if (pagerState.currentPage == 0) {
                IconButton(
                    onClick = onCreateNewProfileClick
                ) {
                    PlusImage(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxSize()
                    )
                }
            }
        },
        bottomBar = {
            PagerAwareSlideAwayBottomBar(
                pagerState = pagerState,
                visibleOnPage = 1
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
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            verticalAlignment = Alignment.Top,
        ) { page ->
            when (page) {
                1 -> Column(
                    modifier = Modifier.padding(paddingValues)
                        .verticalScroll(scrollState),
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    SessionForm(
                        uiState = uiState.toFormUiState(),
                        modifier = Modifier
                            .padding(horizontal = 30.dp),
                        onShowProfilePicker = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        onTaskNameChange = onTaskNameChange,
                        onColorSliderChange = onColorSliderChange,
                        onDifficultyChange = onDifficultyChange,
                        onShowDatePicker = { currentModal = Modal.Date },
                        onDueDateChange = onDueDateChange,
                        onShowTimePicker = { currentModal = Modal.Time },
                        onShowEstimatePicker = { currentModal = Modal.Estimate },
                        onEstimateChange = onEstimateChange,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                0 -> ProfilePicker(
                    profiles = uiState.profiles,
                    modifier = Modifier.padding(paddingValues),
                    onProfileClick = { profileId ->
                        onProfileChange(profileId)
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    onCreateProfileClick = onCreateNewProfileClick,
                )
            }
        }

        when (currentModal) {
            Modal.Discard -> DiscardAlert(
                onDismiss = { currentModal = null },
                onConfirm = onBackClick
            )
            Modal.Date -> {
                DatePickerModal(
                    datePickerState = dueDatePickerState,
                    onValueChange = onDueDateChange,
                    onDismissRequest = { currentModal = null },
                )
            }
            Modal.Time -> {
                TimerPickerModal(
                    timePickerState = dueTimePickerState,
                    onValueChange = onDueTimeChange,
                    onDismissRequest = { currentModal = null },
                )
            }
            Modal.Estimate -> {
                EstimatePickerModal(
                    estimatePickerState = estimatePickerState,
                    onValueChange = onEstimateChange,
                    onDismissRequest = { currentModal = null },
                )
            }
            null -> {}
        }
    }
}


@AspectRatioPreviews
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
                estimate = UserEstimate(15, 2),
                isEstimateEditable = false,
                profiles = DummyData.PROFILES.map { it.toProfilePickerItem() },
                hasFieldChanges = false
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = { },
            onTaskNameChange = { },
            onProfileChange = { },
            onColorSliderChange = { },
            onDifficultyChange = { },
            onDueDateChange = { },
            onDueTimeChange = { },
            onEstimateChange = { },
            onCreateNewProfileClick = { },
            onSaveClick = { true },
        )
    }
}