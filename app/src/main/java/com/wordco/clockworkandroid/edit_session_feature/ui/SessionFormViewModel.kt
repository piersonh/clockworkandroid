package com.wordco.clockworkandroid.edit_session_feature.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asFlow
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.use_case.GetAllProfilesUseCase
import com.wordco.clockworkandroid.core.domain.use_case.GetSessionUseCase
import com.wordco.clockworkandroid.core.ui.util.hue
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.CreateSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetAverageEstimateErrorUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetAverageSessionDurationUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetRemindersForSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.UpdateSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.ui.SessionFormUiEffect.NavigateToProfilePicker
import com.wordco.clockworkandroid.edit_session_feature.ui.model.ReminderListItem
import com.wordco.clockworkandroid.edit_session_feature.ui.model.SessionFormDefaults
import com.wordco.clockworkandroid.edit_session_feature.ui.model.SessionFormModal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toReminderListItem
import com.wordco.clockworkandroid.edit_session_feature.ui.util.toEstimate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.random.Random

class SessionFormViewModel(
    formMode: SessionFormMode,
    private val savedStateHandle: SavedStateHandle,
    private val getSessionUseCase: GetSessionUseCase,
    private val getAllProfilesUseCase: GetAllProfilesUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val updateSessionUseCase: UpdateSessionUseCase,
    private val getAverageSessionDurationUseCase: GetAverageSessionDurationUseCase,
    private val getAverageEstimateErrorUseCase: GetAverageEstimateErrorUseCase,
    private val getRemindersForSessionUseCase: GetRemindersForSessionUseCase,
) : ViewModel() {

    private interface PageBehavior {
        val uiState: StateFlow<SessionFormUiState>
        suspend fun handle(event: SessionFormUiEvent)
    }

    private inner class LoadingBehavior(
        initialState: SessionFormUiState.Retrieving
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
        initialState: SessionFormUiState.Error,
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

    private abstract inner class SessionFormBehavior(
        initialState: SessionFormUiState.Retrieved,
        protected var profileId: Long?,
        protected var getAverageSessionDuration: ((Int) -> Duration)?,
        protected var getAverageEstimateError: ((Int) -> Double)?,
    ) : PageBehavior {
        override val uiState = MutableStateFlow(initialState)

        // Cache for synchronous lookups
        protected var cachedProfiles: List<Profile> = emptyList()

        init {
            // Keep profiles synced for lookups
            viewModelScope.launch {
                getAllProfilesUseCase().collect { profiles ->
                    cachedProfiles = profiles
                }
            }

            viewModelScope.launch {
                savedStateHandle.getLiveData<Long?>(ProfilePickerRoute.RESULT_PROFILE_ID).asFlow()

                    //.getStateFlow<Long?>(ProfilePickerRoute.RESULT_PROFILE_ID, null)
                    .collect { newProfileId ->
                        println("DEBUG: Flow collected profileId: $newProfileId")

                        if (newProfileId != null) {
                            handleProfileChanged(newProfileId)
                            // Clear the result so it doesn't re-trigger on rotation
                            savedStateHandle[ProfilePickerRoute.RESULT_PROFILE_ID] = null
                        }
                    }
            }
        }

        protected abstract suspend fun save(state: SessionFormUiState.Retrieved)

        override suspend fun handle(event: SessionFormUiEvent) {
            when (val e = event as? SessionFormUiEvent.FormEvent) {
                SessionFormUiEvent.BackClicked -> handleBackClick()
                SessionFormUiEvent.ProfileFieldClicked -> sendEffect(NavigateToProfilePicker(profileId))
                is SessionFormUiEvent.TaskNameChanged -> update { copy(taskName = e.newName, hasFormChanges = true) }
                is SessionFormUiEvent.DifficultySliderChanged -> updateDifficulty(e.newPos)
                is SessionFormUiEvent.ColorSliderChanged -> update { copy(colorSliderPos = e.newPos, hasFormChanges = true) }
                is SessionFormUiEvent.DueDateChanged -> updateDueDate(e.newDate)
                is SessionFormUiEvent.DueTimeChanged -> update { copy(dueTime = e.newTime, hasFormChanges = true) }
                is SessionFormUiEvent.EstimateChanged -> update { copy(estimate = e.estimate, hasFormChanges = true) }
                is SessionFormUiEvent.ReminderDateChanged -> updateReminderDate(e.newDate)
                is SessionFormUiEvent.ReminderTimeChanged -> updateReminderTime(e.newTime)


                SessionFormUiEvent.SaveClicked -> validateAndSave()
                SessionFormUiEvent.DiscardConfirmed -> sendEffect(SessionFormUiEffect.NavigateBack)
                SessionFormUiEvent.ModalDismissed -> update { copy(currentModal = null) }
                null -> { }
                SessionFormUiEvent.DueDateFieldClicked -> update { copy(currentModal = SessionFormModal.DueDate) }
                SessionFormUiEvent.DueTimeFieldClicked -> update { copy(currentModal = SessionFormModal.DueTime) }
                SessionFormUiEvent.EstimateFieldClicked -> update { copy(currentModal = SessionFormModal.Estimate) }
                SessionFormUiEvent.ReminderDateFieldClicked -> update { copy(currentModal = SessionFormModal.ReminderDate) }
                SessionFormUiEvent.ReminderTimeFieldClicked -> update { copy(currentModal = SessionFormModal.ReminderTime) }
            }
        }

        private suspend fun handleBackClick() {
            val state = uiState.value
            if (state.hasFormChanges) {
                uiState.update { it.copy(currentModal = SessionFormModal.Discard) }
            } else {
                sendEffect(SessionFormUiEffect.NavigateBack)
            }
        }

        private suspend fun handleProfileChanged(newId: Long?) {
            if (newId == this.profileId) return
            this.profileId = newId

            val newProfile = newId?.let { id -> cachedProfiles.find { it.id == id } }

            getAverageSessionDuration = newId?.let { getAverageSessionDurationUseCase(it) }
            getAverageEstimateError = newId?.let { getAverageEstimateErrorUseCase(it) }

            applyProfileChange(newId, newProfile)
        }

        protected abstract suspend fun applyProfileChange(newId: Long?, newProfile: Profile?)

        private fun updateDifficulty(newDifficulty: Float) {
            val newAverageSessionDuration = getAverageSessionDuration?.invoke(newDifficulty.toInt())
            val newAverageEstimateError = getAverageEstimateError?.invoke(newDifficulty.toInt())

            update {
                copy(
                    difficulty = newDifficulty,
                    hasFormChanges = true,
                    averageSessionDuration = newAverageSessionDuration,
                    averageEstimateError = newAverageEstimateError,
                )
            }
        }

        private fun updateDueDate(epoch: Long?) {
            val date = epoch?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() }
            update { copy(dueDate = date, hasFormChanges = true) }
        }

        private fun updateReminderDate(epoch: Long?) {
            val date = epoch?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() }
            update {
                val currentRem = reminder ?: ReminderListItem(date ?: LocalDate.now(), LocalTime.NOON)
                copy(reminder = currentRem.copy(scheduledDate = date ?: LocalDate.now()), hasFormChanges = true)
            }
        }

        private fun updateReminderTime(time: LocalTime) {
            update {
                val currentRem = reminder ?: ReminderListItem(LocalDate.now(), time)
                copy(reminder = currentRem.copy(scheduledTime = time), hasFormChanges = true)
            }
        }

        private suspend fun validateAndSave() {
            val state = uiState.value
            if (state.taskName.isBlank()) {
                _uiEffect.send(SessionFormUiEffect.ShowSnackbar("Please give the session a name."))
                return
            }
            save(state)
        }

        private fun update(block: SessionFormUiState.Retrieved.() -> SessionFormUiState.Retrieved) {
            uiState.update(block)
        }
    }

    private inner class CreateSessionBehavior(
        initialState: SessionFormUiState.Retrieved,
        initialProfileId: Long?,
        getAverageSessionDuration: ((Int) -> Duration)?,
        getAverageEstimateError: ((Int) -> Double)?,
    ) : SessionFormBehavior(initialState, initialProfileId, getAverageSessionDuration, getAverageEstimateError) {

        private var currentDefaults: SessionFormDefaults

        init {
            val startProfile = initialProfileId?.let { id -> cachedProfiles.find { it.id == id } }
            currentDefaults = getFieldDefaults(startProfile)
        }

        override suspend fun applyProfileChange(newId: Long?, newProfile: Profile?) {
            val newDefaults = getFieldDefaults(newProfile)
            val oldDefaults = currentDefaults

            val difficulty = uiState.value.difficulty.toInt()
            val newAverageSessionDuration = getAverageSessionDuration?.invoke(difficulty)
            val newAverageEstimateError = getAverageEstimateError?.invoke(difficulty)

            uiState.update { state ->
                val updatedTaskName = if (state.taskName == oldDefaults.taskName) {
                    newDefaults.taskName
                } else state.taskName

                val updatedColor = if (state.colorSliderPos == oldDefaults.colorSliderPos) {
                    newDefaults.colorSliderPos
                } else state.colorSliderPos

                val updatedDifficulty = if (state.difficulty == oldDefaults.difficulty) {
                    newDefaults.difficulty
                } else state.difficulty

                state.copy(
                    profileName = newProfile?.name,
                    taskName = updatedTaskName,
                    colorSliderPos = updatedColor,
                    difficulty = updatedDifficulty,
                    averageSessionDuration = newAverageSessionDuration,
                    averageEstimateError = newAverageEstimateError,
                    hasFormChanges = true
                )
            }

            currentDefaults = newDefaults
        }

        override suspend fun save(state: SessionFormUiState.Retrieved) {
            val newTask = NewTask(
                taskId = 0,
                name = state.taskName,
                dueDate = buildInstant(state),
                difficulty = state.difficulty.toInt(),
                color = Color.hsv(state.colorSliderPos * 360, 1f, 1f),
                userEstimate = state.estimate?.toDuration(),
                profileId = profileId,
                appEstimate = null
            )
            createSessionUseCase(newTask, extractReminders(state))
            sendEffect(SessionFormUiEffect.NavigateBack)
        }
    }

    private inner class EditSessionBehavior(
        initialState: SessionFormUiState.Retrieved,
        val originalSession: Task,
        getAverageSessionDuration: ((Int) -> Duration)?,
        getAverageEstimateError: ((Int) -> Double)?,
    ) : SessionFormBehavior(initialState, originalSession.profileId, getAverageSessionDuration, getAverageEstimateError) {
        override suspend fun applyProfileChange(newId: Long?, newProfile: Profile?) {
            val difficulty = uiState.value.difficulty.toInt()
            val newAverageSessionDuration = getAverageSessionDuration?.invoke(difficulty)
            val newAverageEstimateError = getAverageEstimateError?.invoke(difficulty)

            uiState.update { state ->
                state.copy(
                    profileName = newProfile?.name,
                    averageSessionDuration = newAverageSessionDuration,
                    averageEstimateError = newAverageEstimateError,
                    hasFormChanges = true
                )
            }
        }


        override suspend fun save(state: SessionFormUiState.Retrieved) {
            val updatedTask = when(originalSession) {
                is NewTask -> originalSession.copy(
                    name = state.taskName,
                    dueDate = buildInstant(state),
                    difficulty = state.difficulty.toInt(),
                    color = Color.hsv(state.colorSliderPos * 360, 1f, 1f),
                    userEstimate = state.estimate?.toDuration(),
                    profileId = profileId
                )
                is StartedTask -> originalSession.copy(
                    name = state.taskName,
                    dueDate = buildInstant(state),
                    difficulty = state.difficulty.toInt(),
                    color = Color.hsv(state.colorSliderPos * 360, 1f, 1f),
                    profileId = profileId
                )
                is CompletedTask -> originalSession.copy(
                    name = state.taskName,
                    dueDate = buildInstant(state),
                    difficulty = state.difficulty.toInt(),
                    color = Color.hsv(state.colorSliderPos * 360, 1f, 1f),
                    profileId = profileId
                )
            }
            updateSessionUseCase(updatedTask, extractReminders(state))
            sendEffect(SessionFormUiEffect.NavigateBack)
        }
    }

    private fun buildInstant(state: SessionFormUiState.Retrieved): Instant? {
        val date = state.dueDate ?: return null
        val time = state.dueTime

        return LocalDateTime.of(date, time)
            .atZone(ZoneId.systemDefault())
            .toInstant()
    }

    private fun extractReminders(state: SessionFormUiState.Retrieved): List<Instant> {
        val reminder = state.reminder ?: return emptyList()

        val instant = LocalDateTime.of(reminder.scheduledDate, reminder.scheduledTime)
            .atZone(ZoneId.systemDefault())
            .toInstant()

        return listOf(instant)
    }

    private val _currentBehavior = MutableStateFlow<PageBehavior>(
        LoadingBehavior(SessionFormUiState.Retrieving(
            when (formMode) {
                is SessionFormMode.Create -> "Create New Session"
                is SessionFormMode.Edit -> "Edit Session"
            }
        ))
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
            try {
                val initialProfiles = getAllProfilesUseCase().first()

                when (formMode) {
                    is SessionFormMode.Create -> setupCreate(formMode, initialProfiles)
                    is SessionFormMode.Edit -> setupEdit(formMode, initialProfiles)
                }
            } catch (e: Exception) {
                setFailure(
                    alert = "Failed to load session",
                    message = e.message ?: "Unknown error",
                    trace = e.stackTraceToString()
                )
            }
        }
    }

    private suspend fun setupCreate(mode: SessionFormMode.Create, profiles: List<Profile>) {
        val profile = mode.profileId?.let { id -> profiles.find { it.id == id } }

        val defaults = getFieldDefaults(profile)

        val getAverageSessionDuration = profile?.let { getAverageSessionDurationUseCase(it.id) }
        val getAverageEstimateError = profile?.let { getAverageEstimateErrorUseCase(it.id) }

        val initialState = SessionFormUiState.Retrieved(
            title = "Create New Session",
            taskName = defaults.taskName,
            profileName = defaults.profileName,
            colorSliderPos = defaults.colorSliderPos,
            difficulty = defaults.difficulty,
            dueDate = null,
            dueTime = defaults.dueTime,
            estimate = defaults.estimate,
            isEstimateEditable = true,
            averageSessionDuration = getAverageSessionDuration?.invoke(defaults.difficulty.toInt()),
            averageEstimateError = getAverageEstimateError?.invoke(defaults.difficulty.toInt()),
            reminder = null,
            hasFormChanges = false
        )

        _currentBehavior.update {
            CreateSessionBehavior(
                initialState = initialState,
                initialProfileId = profile?.id,
                getAverageSessionDuration,
                getAverageEstimateError,
            )
        }

        if (profile == null) {
            sendEffect(SessionFormUiEffect.NavigateToProfilePicker(null))
        }
    }

    private suspend fun setupEdit(mode: SessionFormMode.Edit, profiles: List<Profile>) {
        val session = getSessionUseCase(mode.sessionId).first()
        val profile = profiles.find { it.id == session.profileId }

        val existingReminder = getRemindersForSessionUseCase(session.taskId)
            .first()
            .firstOrNull()
            ?.toReminderListItem()

        val getAverageSessionDuration = profile?.let { getAverageSessionDurationUseCase(it.id) }
        val getAverageEstimateError = profile?.let { getAverageEstimateErrorUseCase(it.id) }

        val initialState = SessionFormUiState.Retrieved(
            title = "Edit Session",
            isEstimateEditable = session is NewTask,
            taskName = session.name,
            profileName = profile?.name,
            colorSliderPos = session.color.hue() / 360f,
            difficulty = session.difficulty.toFloat(),
            dueDate = session.dueDate?.atZone(ZoneId.systemDefault())?.toLocalDate(),
            dueTime = session.dueDate?.atZone(ZoneId.systemDefault())?.toLocalTime() ?: LocalTime.of(23, 59),
            estimate = session.userEstimate?.toEstimate(),
            averageSessionDuration = getAverageSessionDuration?.invoke(session.difficulty),
            averageEstimateError = getAverageEstimateError?.invoke(session.difficulty),
            reminder = existingReminder,
            hasFormChanges = false
        )

        _currentBehavior.update {
            EditSessionBehavior(
                initialState = initialState,
                originalSession = session,
                getAverageSessionDuration,
                getAverageEstimateError,
            )
        }
    }

    private fun setFailure(alert: String, message: String, trace: String) {
        _currentBehavior.update { currentBehavior ->
            ErrorBehavior(
                SessionFormUiState.Error(
                    title = currentBehavior.uiState.value.title,
                    header = alert,
                    message = message
                ),
                stackTrace = trace
            )
        }
    }


    private fun getFieldDefaults(
        profile: Profile?
    ): SessionFormDefaults {
        return if (profile == null) {
            SessionFormDefaults(
                taskName = "",
                profileName = null,
                colorSliderPos = Random.nextFloat(),
                difficulty = 0f,
                dueTime = LocalTime.of(23, 59),
                estimate = null,
                reminderTime = LocalTime.of(12,0),
            )
        } else {
            SessionFormDefaults(
                // TODO replace this with a truth from the database so that the quantity
                //  is retained after sessions are deleted or switched profiles
                taskName = "${profile.name} ${profile.sessions.size + 1}",
                profileName = profile.name,
                colorSliderPos = profile.color.hue().div(360),
                difficulty = profile.defaultDifficulty.toFloat(),
                dueTime = LocalTime.of(23, 59),
                estimate = null,
                reminderTime = LocalTime.of(12,0),
            )
        }
    }

    fun onEvent(event: SessionFormUiEvent) {
        viewModelScope.launch { _currentBehavior.value.handle(event) }
    }

    private suspend fun sendEffect(effect: SessionFormUiEffect) {
        _uiEffect.send(effect)
    }


    companion object {
        val FORM_MODE_KEY = object : CreationExtras.Key<SessionFormMode> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer

                val formMode = this[FORM_MODE_KEY] as SessionFormMode

                val savedStateHandle = createSavedStateHandle()

                SessionFormViewModel(
                    formMode = formMode,
                    getSessionUseCase = appContainer.getSessionUseCase,
                    getAllProfilesUseCase = appContainer.getAllProfilesUseCase,
                    createSessionUseCase = appContainer.createSessionUseCase,
                    updateSessionUseCase = appContainer.updateSessionUseCase,
                    getAverageSessionDurationUseCase = appContainer.getAverageSessionDurationUseCase,
                    getAverageEstimateErrorUseCase = appContainer.getAverageEstimateErrorUseCase,
                    getRemindersForSessionUseCase = appContainer.getRemindersForSessionUseCase,
                    savedStateHandle = savedStateHandle,
                )
            }
        }
    }
}