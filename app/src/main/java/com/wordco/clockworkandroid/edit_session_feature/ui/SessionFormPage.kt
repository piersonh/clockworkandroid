package com.wordco.clockworkandroid.edit_session_feature.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
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
import com.wordco.clockworkandroid.core.ui.composables.AccentRectangleTextButton
import com.wordco.clockworkandroid.core.ui.composables.DiscardAlert
import com.wordco.clockworkandroid.core.ui.composables.PlusImage
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews
import com.wordco.clockworkandroid.edit_session_feature.ui.SessionFormEvent.DueDateChanged
import com.wordco.clockworkandroid.edit_session_feature.ui.SessionFormEvent.DueTimeChanged
import com.wordco.clockworkandroid.edit_session_feature.ui.SessionFormEvent.EstimateChanged
import com.wordco.clockworkandroid.edit_session_feature.ui.SessionFormEvent.ProfileChanged
import com.wordco.clockworkandroid.edit_session_feature.ui.SessionFormEvent.ReminderDateChanged
import com.wordco.clockworkandroid.edit_session_feature.ui.SessionFormEvent.ReminderTimeChanged
import com.wordco.clockworkandroid.edit_session_feature.ui.SessionFormEvent.SaveClicked
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.DatePickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.EstimatePickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.PagerAwareSlideAwayBottomBar
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.ProfilePicker
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.SessionForm
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.SessionFormPageScaffold
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.TimerPickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.composables.rememberEstimatePickerState
import com.wordco.clockworkandroid.edit_session_feature.ui.model.Modal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.util.toEstimate
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

@Composable
fun SessionFormPage(
    onBackClick: () -> Unit,
    onCreateProfileClick: () -> Unit,
    viewModel: SessionFormViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    SessionFormPage(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onCreateNewProfileClick = onCreateProfileClick,
        onEvent = viewModel::onEvent,
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SessionFormEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is SessionFormEffect.NavigateBack -> {
                    onBackClick()
                }
            }
        }
    }
}


@Composable
private fun SessionFormPage(
    uiState: SessionFormUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onCreateNewProfileClick: () -> Unit,
    onEvent: (SessionFormEvent) -> Unit,
) {
    when (uiState) {
        is SessionFormUiState.Retrieving -> SessionFormPageScaffold(
            title = uiState.title,
            onBackClick = onBackClick,
            snackbarHostState = snackbarHostState,
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    Text("Loading...")
                }
            },
        )

        is SessionFormUiState.Retrieved -> SessionFormPageRetrieved(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
            onCreateNewProfileClick = onCreateNewProfileClick,
            onEvent = onEvent
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionFormPageRetrieved(
    uiState: SessionFormUiState.Retrieved,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onCreateNewProfileClick: () -> Unit,
    onEvent: (SessionFormEvent) -> Unit,
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = uiState.initialPage,
        pageCount = { 2 }
    )

    val dueDatePickerState = rememberDatePickerState()
    val dueTimePickerState = rememberTimePickerState(
        initialHour = uiState.dueTime?.hour ?: 0,
        initialMinute = uiState.dueTime?.minute ?: 0
    )
    val estimatePickerState = rememberEstimatePickerState(
        initialValue = uiState.estimate
            ?: uiState.averageSessionDuration?.toEstimate()
            ?: UserEstimate(0,0)
    )
    val reminderDatePickerState = rememberDatePickerState()
    val reminderTimePickerState = rememberTimePickerState(
        initialHour = uiState.dueTime?.hour ?: 0,
        initialMinute = uiState.dueTime?.minute ?: 0
    )
    var currentModal by remember { mutableStateOf<Modal?>(null) }
    val onBackClickCheckChanges = {
        if (uiState.hasFieldChanges) {
            currentModal = Modal.Discard
        } else {
            onBackClick()
        }
    }

    // update displayed values on changes

    LaunchedEffect(uiState.dueDate) {
        uiState.dueDate?.let {
            dueDatePickerState.selectedDateMillis = it.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
    }

    LaunchedEffect(uiState.dueTime) {
        uiState.dueTime?.let {
            dueTimePickerState.hour = it.hour
            dueTimePickerState.minute = it.minute
        }
    }

    LaunchedEffect(uiState.estimate, uiState.averageSessionDuration) {
        val value = uiState.estimate
            ?: uiState.averageSessionDuration?.toEstimate()
            ?: UserEstimate(0,0)

        val hoursState = estimatePickerState.hoursState
        val newHourIndex = hoursState.items.indexOf(value.hours)
        if (newHourIndex != -1 && hoursState.centeredItemIndex != newHourIndex) {
            hoursState.scrollToListItemIndex(newHourIndex)
        }

        val minutesState = estimatePickerState.minutesState
        val newMinuteIndex = minutesState.items.indexOf(value.minutes)
        if (newMinuteIndex != -1 && minutesState.centeredItemIndex != newMinuteIndex) {
            minutesState.scrollToListItemIndex(newMinuteIndex)
        }
    }

    LaunchedEffect(uiState.reminder) {
        uiState.reminder?.let {
            dueDatePickerState.selectedDateMillis = it.scheduledDate.atStartOfDay(ZoneOffset.UTC)
                .toInstant().toEpochMilli()
            dueTimePickerState.hour = it.scheduledTime.hour
            dueTimePickerState.minute = it.scheduledTime.minute
        }
    }


    BackHandler(enabled = uiState.hasFieldChanges) {
        currentModal = Modal.Discard
    }

    SessionFormPageScaffold(
        title = uiState.title,
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
                            onEvent(SaveClicked)
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
                    modifier = Modifier
                        .padding(paddingValues)
                        .verticalScroll(scrollState),
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    SessionForm(
                        uiState = uiState,
                        modifier = Modifier
                            .padding(horizontal = 30.dp),
                        onEvent = onEvent,
                        onShowProfilePicker = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = 0,
                                    animationSpec = tween(300)
                                )
                            }
                        },
                        onShowDueDatePicker = { currentModal = Modal.DueDate },
                        onShowDueTimePicker = { currentModal = Modal.DueTime },
                        onShowEstimatePicker = { currentModal = Modal.Estimate },
                        onShowReminderDatePicker = { currentModal = Modal.ReminderDate },
                        onShowReminderTimePicker = { currentModal = Modal.ReminderTime },
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                0 -> ProfilePicker(
                    profiles = uiState.profiles,
                    modifier = Modifier.padding(paddingValues),
                    onProfileClick = { profileId ->
                        onEvent(ProfileChanged(profileId))
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                page = 1,
                                animationSpec = tween(300)
                            )
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
            Modal.DueDate -> {
                DatePickerModal(
                    datePickerState = dueDatePickerState,
                    onValueChange = { onEvent(DueDateChanged(it)) },
                    onDismissRequest = { currentModal = null },
                )
            }
            Modal.DueTime -> {
                TimerPickerModal(
                    timePickerState = dueTimePickerState,
                    onValueChange = { onEvent(DueTimeChanged(it)) },
                    onDismissRequest = { currentModal = null },
                )
            }
            Modal.Estimate -> {
                EstimatePickerModal(
                    estimatePickerState = estimatePickerState,
                    onValueChange = { onEvent(EstimateChanged(it)) },
                    onDismissRequest = { currentModal = null },
                    averageSessionDuration = uiState.averageSessionDuration,
                    averageEstimateError = uiState.averageEstimateError,
                )
            }
            null -> {}
            Modal.ReminderDate -> {
                DatePickerModal(
                    datePickerState = reminderDatePickerState,
                    onValueChange = { onEvent(ReminderDateChanged(it)) },
                    onDismissRequest = { currentModal = null },
                )
            }
            Modal.ReminderTime -> {
                TimerPickerModal(
                    timePickerState = reminderTimePickerState,
                    onValueChange = { onEvent(ReminderTimeChanged(it)) },
                    onDismissRequest = { currentModal = null },
                )
            }
        }
    }
}

@AspectRatioPreviews
@Composable
private fun SessionFormPagePreview() {
    ClockworkTheme {
        SessionFormPageRetrieved(
            uiState = SessionFormUiState.Retrieved(
                title = "Preview",
                initialPage = 1,
                profiles = listOf(),
                taskName = "Preview",
                profileName = "Preview",
                colorSliderPos = 0.5f,
                difficulty = 1f,
                dueDate = LocalDate.of(2025, 10, 31),
                dueTime = LocalTime.of(23,59),
                estimate = UserEstimate(
                    minutes = 1,
                    hours = 2
                ),
                isEstimateEditable = true,
                hasFieldChanges = false,
                averageSessionDuration = Duration.ofSeconds(1234),
                averageEstimateError = null,
                reminder = null,
            ),
            snackbarHostState = SnackbarHostState(),
            onBackClick = {},
            onCreateNewProfileClick = {},
            onEvent = {}
        )
    }
}