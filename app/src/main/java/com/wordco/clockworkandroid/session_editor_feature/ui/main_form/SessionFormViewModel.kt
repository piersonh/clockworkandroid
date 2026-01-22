package com.wordco.clockworkandroid.session_editor_feature.ui.main_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordco.clockworkandroid.session_editor_feature.coordinator.SessionEditorManager
import com.wordco.clockworkandroid.session_editor_feature.coordinator.SessionEditorState
import com.wordco.clockworkandroid.session_editor_feature.domain.model.DraftValidationError
import com.wordco.clockworkandroid.session_editor_feature.domain.model.ReminderDraft
import com.wordco.clockworkandroid.session_editor_feature.domain.model.SessionDraft
import com.wordco.clockworkandroid.session_editor_feature.domain.use_case.GetAverageEstimateErrorUseCase
import com.wordco.clockworkandroid.session_editor_feature.domain.use_case.GetAverageSessionDurationUseCase
import com.wordco.clockworkandroid.session_editor_feature.ui.main_form.SessionFormUiState.Error
import com.wordco.clockworkandroid.session_editor_feature.ui.main_form.SessionFormUiState.Retrieved
import com.wordco.clockworkandroid.session_editor_feature.ui.main_form.SessionFormUiState.Retrieving
import com.wordco.clockworkandroid.session_editor_feature.ui.main_form.model.SessionFormModal
import com.wordco.clockworkandroid.session_editor_feature.ui.reminder_list.model.mapper.toReminderListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

class SessionFormViewModel(
    private val editorManager: SessionEditorManager,
    private val getAverageSessionDurationUseCase: GetAverageSessionDurationUseCase,
    private val getAverageEstimateErrorUseCase: GetAverageEstimateErrorUseCase,
) : ViewModel() {
    private interface PageBehavior {
        val uiState: StateFlow<SessionFormUiState>
        suspend fun handle(event: SessionFormUiEvent)
    }

    private inner class LoadingBehavior(
        initialState: Retrieving
    ) : PageBehavior {
        override val uiState = MutableStateFlow(initialState)

        override suspend fun handle(event: SessionFormUiEvent) {
            when (event as? SessionFormUiEvent.LoadingEvent) {
                SessionFormUiEvent.BackClicked -> sendEffect(SessionFormUiEffect.NavigateBack)
                null -> { }
            }
        }
    }

    private inner class ErrorBehavior(
        initialState: Error,
        val stackTrace: String?
    ) : PageBehavior {
        override val uiState = MutableStateFlow(initialState)

        override suspend fun handle(event: SessionFormUiEvent) {
            when (event as? SessionFormUiEvent.ErrorEvent) {
                SessionFormUiEvent.BackClicked -> _uiEffect.send(SessionFormUiEffect.NavigateBack)
                SessionFormUiEvent.CopyErrorClicked -> copyError()
                null -> { }
            }
        }

        private suspend fun copyError() {
            val clipboardContent = """
                    Title: ${uiState.value.header}
                    Message: ${uiState.value.message}
                    --- StackTrace ---
                    $stackTrace
                """.trimIndent()

            sendEffect(SessionFormUiEffect.CopyToClipboard(clipboardContent))
            sendEffect(SessionFormUiEffect.ShowSnackbar("Error info copied"))
        }
    }

    private inner class SessionFormBehavior(
        initialState: Retrieved,
        private var profileId: Long?,
        private var getAverageSessionDuration: ((Int) -> Duration)?,
        private var getAverageEstimateError: ((Int) -> Double)?,
    ) : PageBehavior {
        override val uiState = MutableStateFlow(initialState)

        override suspend fun handle(event: SessionFormUiEvent) {
            when (val e = event as? SessionFormUiEvent.FormEvent) {
                SessionFormUiEvent.BackClicked -> handleBackClick()
                SessionFormUiEvent.ProfileFieldClicked -> sendEffect(SessionFormUiEffect.NavigateToProfilePicker)
                is SessionFormUiEvent.TaskNameChanged -> editorManager.updateName(e.newName)
                is SessionFormUiEvent.DifficultySliderChanged -> updateDifficulty(e.newPos)
                is SessionFormUiEvent.ColorSliderChanged -> editorManager.updateColor(e.newPos)
                is SessionFormUiEvent.DueDateChanged -> updateDueDate(e.newDate)
                is SessionFormUiEvent.DueTimeChanged -> editorManager.updateDueTime(e.newTime)
                is SessionFormUiEvent.EstimateChanged -> editorManager.updateEstimate(e.estimate)
                is SessionFormUiEvent.ReminderDateChanged -> updateReminderDate(e.newDate)
                is SessionFormUiEvent.ReminderTimeChanged -> editorManager.updateReminderTime(e.newTime)

                SessionFormUiEvent.SaveClicked -> validateAndSave()
                SessionFormUiEvent.DiscardConfirmed -> sendEffect(SessionFormUiEffect.NavigateBack)
                SessionFormUiEvent.ModalDismissed -> update { copy(currentModal = null) }

                SessionFormUiEvent.DueDateFieldClicked -> update { copy(currentModal = SessionFormModal.DueDate) }
                SessionFormUiEvent.DueTimeFieldClicked -> update { copy(currentModal = SessionFormModal.DueTime) }
                SessionFormUiEvent.EstimateFieldClicked -> update { copy(currentModal = SessionFormModal.Estimate) }
                SessionFormUiEvent.ReminderDateFieldClicked -> update { copy(currentModal = SessionFormModal.ReminderDate) }
                SessionFormUiEvent.ReminderTimeFieldClicked -> update { copy(currentModal = SessionFormModal.ReminderTime) }

                null -> { }
            }
        }

        private suspend fun handleBackClick() {
            val state = editorManager.state.value
            if (state is SessionEditorState.Retrieved && state.hasUnsavedChanges) {
                uiState.update { it.copy(currentModal = SessionFormModal.Discard) }
            } else {
                sendEffect(SessionFormUiEffect.NavigateBack)
            }
        }

        private fun updateDifficulty(newDifficulty: Float) {
            val newAverageSessionDuration = getAverageSessionDuration?.invoke(newDifficulty.toInt())
            val newAverageEstimateError = getAverageEstimateError?.invoke(newDifficulty.toInt())

            update {
                copy(
                    averageSessionDuration = newAverageSessionDuration,
                    averageEstimateError = newAverageEstimateError,
                )
            }

            editorManager.updateDifficulty(newDifficulty.toInt())
        }

        private fun updateDueDate(epoch: Long?) {
            val date = epoch?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() }
            editorManager.updateDueDate(date)
        }

        private fun updateReminderDate(epoch: Long?) {
            val date = epoch?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() }
            editorManager.updateReminderDate(date)
        }

        private suspend fun validateAndSave() {
            val validationErrors = editorManager.validate()
            if (validationErrors.isEmpty()) {
                editorManager.save()
                sendEffect(SessionFormUiEffect.NavigateBack)
            } else {
                val firstError = validationErrors.first()
                val snackbarEffect = SessionFormUiEffect.ShowSnackbar(
                    message = when (firstError) {
                        DraftValidationError.EditorNotLoaded -> "Please wait for the editor to load."
                        DraftValidationError.EmptyName -> "Please give the session a name."
                    }
                )

                sendEffect(snackbarEffect)
            }
        }

        private fun update(block: Retrieved.() -> Retrieved) {
            uiState.update(block)
        }

        suspend fun updateUi(draft: SessionDraft, profileName: String?, reminder: ReminderDraft?) {
            if (draft.profileId != this.profileId) {
                getAverageSessionDuration = draft.profileId?.let { getAverageSessionDurationUseCase(it) }
                getAverageEstimateError = draft.profileId?.let { getAverageEstimateErrorUseCase(it) }

                val newAverageSessionDuration = getAverageSessionDuration?.invoke(draft.difficulty)
                val newAverageEstimateError = getAverageEstimateError?.invoke(draft.difficulty)

                update {
                    copy(
                        taskName = draft.sessionName,
                        profileName = profileName,
                        colorSliderPos = draft.colorHue,
                        difficulty = draft.difficulty.toFloat(),
                        dueDate = draft.dueDateTime?.toLocalDate(),
                        dueTime = draft.dueDateTime?.toLocalTime(),
                        estimate = draft.estimate,
                        reminder = reminder?.toReminderListItem(),
                        averageSessionDuration = newAverageSessionDuration,
                        averageEstimateError = newAverageEstimateError,
                    )
                }
            } else {
                update {
                    copy(
                        taskName = draft.sessionName,
                        profileName = profileName,
                        colorSliderPos = draft.colorHue,
                        difficulty = draft.difficulty.toFloat(),
                        dueDate = draft.dueDateTime?.toLocalDate(),
                        dueTime = draft.dueDateTime?.toLocalTime(),
                        estimate = draft.estimate,
                        reminder = reminder?.toReminderListItem(),
                    )
                }
            }
        }
    }

    private val _currentBehavior = MutableStateFlow<PageBehavior>(
        LoadingBehavior(
            Retrieving(
                when (editorManager) {
                    is SessionEditorManager.Create -> "Create New Session"
                    is SessionEditorManager.Edit -> "Edit Session"
                }
            )
        )
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = _currentBehavior
        .flatMapLatest { behavior -> behavior.uiState }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            _currentBehavior.value.uiState.value
        )

    private val _uiEffect = Channel<SessionFormUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()


    init {
        viewModelScope.launch {
            startCollection()
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun startCollection() {
        editorManager.state.onEach { editorState ->
            when (editorState) {
                is SessionEditorState.Error -> setFailure(
                    alert = editorState.alert,
                    message = editorState.error.message ?: "No Message",
                    trace = editorState.error.stackTraceToString(),
                )
                SessionEditorState.Retrieving -> setLoading()
                is SessionEditorState.Retrieved -> {
                    val draft = editorState.draft
                    val reminders = editorState.reminders
                    val profile = editorState.activeProfile

                    val currentBehavior = _currentBehavior.value
                    if (currentBehavior is SessionFormBehavior) {
                        currentBehavior.updateUi(
                            draft,
                            profile?.name,
                            reminders.firstOrNull(),
                        )
                    } else {
                        val getAverageSessionDuration = profile?.let { getAverageSessionDurationUseCase(it.id) }
                        val getAverageEstimateError = profile?.let { getAverageEstimateErrorUseCase(it.id) }

                        _currentBehavior.update {
                            SessionFormBehavior(
                                initialState = Retrieved(
                                    title = currentBehavior.uiState.value.title,
                                    isEstimateEditable = editorState.isEstimateEditable,
                                    averageSessionDuration = getAverageSessionDuration?.invoke(draft.difficulty),
                                    averageEstimateError = getAverageEstimateError?.invoke(draft.difficulty),
                                    taskName = draft.sessionName,
                                    profileName = profile?.name,
                                    colorSliderPos = draft.colorHue,
                                    difficulty = draft.difficulty.toFloat(),
                                    dueDate = draft.dueDateTime?.toLocalDate(),
                                    dueTime = draft.dueDateTime?.toLocalTime(),
                                    estimate = draft.estimate,
                                    reminder = reminders.firstOrNull()?.toReminderListItem(),
                                    currentModal = null
                                ),
                                draft.profileId,
                                getAverageSessionDuration = getAverageSessionDuration,
                                getAverageEstimateError = getAverageEstimateError
                            )
                        }
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    // TODO: snackbar notify additional errors suppressed if occur
    private fun setFailure(alert: String, message: String, trace: String) {
        val currentBehavior = _currentBehavior.value
        if (currentBehavior is ErrorBehavior) {
            return
        }

        _currentBehavior.update { currentBehavior ->
            ErrorBehavior(
                Error(
                    title = currentBehavior.uiState.value.title,
                    header = alert,
                    message = message
                ),
                stackTrace = trace
            )
        }
    }

    private fun setLoading() {
        val currentBehavior = _currentBehavior.value
        if (currentBehavior is LoadingBehavior) {
            return
        }

        _currentBehavior.update { currentBehavior ->
            LoadingBehavior(
                Retrieving(
                    currentBehavior.uiState.value.title
                )
            )
        }
    }


    fun onEvent(event: SessionFormUiEvent) {
        viewModelScope.launch { _currentBehavior.value.handle(event) }
    }

    private suspend fun sendEffect(effect: SessionFormUiEffect) {
        _uiEffect.send(effect)
    }
}