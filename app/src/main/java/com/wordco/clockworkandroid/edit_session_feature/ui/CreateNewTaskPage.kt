package com.wordco.clockworkandroid.edit_session_feature.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.wordco.clockworkandroid.core.ui.composables.AccentRectangleTextButton
import com.wordco.clockworkandroid.core.ui.composables.PlusImage
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.Fallible
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.DiscardAlert
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.EditPageScaffold
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.ProfilePicker
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.SessionForm
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.SlideAwayBottomBar
import com.wordco.clockworkandroid.edit_session_feature.ui.model.Modal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.SaveSessionError
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.util.tweenToPage
import kotlinx.coroutines.launch
import java.time.LocalTime

@Composable
fun CreateNewTaskPage (
    onBackClick: () -> Unit,
    onCreateNewProfileClick: () -> Unit,
    viewModel: CreateNewTaskViewModel,
    skipProfilePicker: Boolean = false,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateTaskPage(
        uiState = uiState,
        onBackClick = onBackClick,
        initialPage = if (skipProfilePicker) 1 else 0,
        onTaskNameChange = viewModel::onTaskNameChange,
        onProfileChange = viewModel::onProfileChange,
        onColorSliderChange = viewModel::onColorSliderChange,
        onDifficultyChange = viewModel::onDifficultyChange,
        onShowDatePicker = viewModel::onShowDatePicker,
        onDismissModal = viewModel::onDismissModal,
        onDueDateChange = viewModel::onDueDateChange,
        onShowTimePicker = viewModel::onShowTimePicker,
        onDueTimeChange = viewModel::onDueTimeChange,
        onEstimateChange = viewModel::onEstimateChange,
        onShowEstimatePicker = viewModel::onShowEstimatePicker,
        onCreateNewProfileClick = onCreateNewProfileClick,
        onShowDiscardAlert = viewModel::onShowDiscardAlert,
        onSaveClick = viewModel::onSaveClick,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTaskPage(
    uiState: CreateNewSessionUiState,
    onBackClick: () -> Unit,
    initialPage: Int = 1,
    onTaskNameChange: (String) -> Unit,
    onProfileChange: (Long?) -> Unit,
    onColorSliderChange: (Float) -> Unit,
    onDifficultyChange: (Float) -> Unit,
    onShowDatePicker: () -> Unit,
    onDismissModal: () -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onShowTimePicker: () -> Unit,
    onDueTimeChange: (LocalTime) -> Unit,
    onEstimateChange: (UserEstimate?) -> Unit,
    onShowEstimatePicker: () -> Unit,
    onCreateNewProfileClick: () -> Unit,
    onShowDiscardAlert: () -> Boolean,
    onSaveClick: () -> Fallible<SaveSessionError>,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    when (uiState) {
        CreateNewSessionUiState.Retrieving -> EditPageScaffold(
            title = "Create New Session",
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

        is CreateNewSessionUiState.Retrieved -> CreateSessionPageRetrieved(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onBackClick = {
                if (uiState.currentModal == Modal.Discard || !onShowDiscardAlert()) {
                    onBackClick()
                }
            },
            onProfileChange = onProfileChange,
            initialPage = initialPage,
            onTaskNameChange = onTaskNameChange,
            onColorSliderChange = onColorSliderChange,
            onDifficultyChange = onDifficultyChange,
            onShowDatePicker = onShowDatePicker,
            onDismissModal = onDismissModal,
            onDueDateChange = onDueDateChange,
            onShowTimePicker = onShowTimePicker,
            onDueTimeChange = onDueTimeChange,
            onEstimateChange = onEstimateChange,
            onShowEstimatePicker = onShowEstimatePicker,
            onCreateNewProfileClick = onCreateNewProfileClick,
            onSaveClick = onSaveClick,
        )
    }
}

@Composable
private fun CreateSessionPageRetrieved(
    uiState: CreateNewSessionUiState.Retrieved,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onProfileChange: (Long?) -> Unit,
    initialPage: Int,
    onTaskNameChange: (String) -> Unit,
    onColorSliderChange: (Float) -> Unit,
    onDifficultyChange: (Float) -> Unit,
    onShowDatePicker: () -> Unit,
    onDismissModal: () -> Unit,
    onDueDateChange: (Long?) -> Unit,
    onShowTimePicker: () -> Unit,
    onDueTimeChange: (LocalTime) -> Unit,
    onEstimateChange: (UserEstimate?) -> Unit,
    onShowEstimatePicker: () -> Unit,
    onCreateNewProfileClick: () -> Unit,
    onSaveClick: () -> Fallible<SaveSessionError>,
) {
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(
        initialPage = if (uiState.profiles.isEmpty()) 1 else initialPage,
        pageCount = { 2 }
    )
    val coroutineScope = rememberCoroutineScope()
    var isBottomBarVisible by remember {
        mutableStateOf(pagerState.targetPage == 1)
    }

    EditPageScaffold(
        title = "Create New Session",
        onBackClick = onBackClick,
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
            SlideAwayBottomBar(
                isBottomBarVisible = isBottomBarVisible
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    AccentRectangleTextButton(
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
                1 -> Column {
                    Box(
                        modifier = Modifier.padding(paddingValues),
                    ) {
                        SessionForm(
                            uiState = uiState.toFormUiState(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp, vertical = 20.dp)
                                .verticalScroll(scrollState),
                            onShowProfilePicker = {
                                isBottomBarVisible = false
                                coroutineScope.launch {
                                    pagerState.tweenToPage(0)
                                }
                            },
                            onTaskNameChange = onTaskNameChange,
                            onColorSliderChange = onColorSliderChange,
                            onDifficultyChange = onDifficultyChange,
                            onShowDatePicker = onShowDatePicker,
                            onDismissDatePicker = onDismissModal,
                            onDueDateChange = onDueDateChange,
                            onShowTimePicker = onShowTimePicker,
                            onDismissTimePicker = onDismissModal,
                            onDueTimeChange = onDueTimeChange,
                            onShowEstimatePicker = onShowEstimatePicker,
                            onDismissEstimatePicker = onDismissModal,
                            onEstimateChange = onEstimateChange,
                        )
                    }
                }

                0 -> ProfilePicker(
                    profiles = uiState.profiles,
                    modifier = Modifier.padding(paddingValues),
                    onProfileClick = { profileId ->
                        onProfileChange(profileId)
                        coroutineScope.launch {
                            pagerState.tweenToPage(1)
                        }
                        isBottomBarVisible = true
                    },
                    onCreateProfileClick = onCreateNewProfileClick,
                )
            }
        }

        if (uiState.currentModal == Modal.Discard) {
            DiscardAlert(
                onDismiss = onDismissModal,
                onConfirm = onBackClick
            )
        }
    }
}