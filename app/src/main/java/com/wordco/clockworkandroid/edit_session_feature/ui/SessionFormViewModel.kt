package com.wordco.clockworkandroid.edit_session_feature.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
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
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.core.ui.util.getIfType
import com.wordco.clockworkandroid.core.ui.util.hue
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.InsertNewSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.UpdateSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.ui.model.SessionFormDefaults
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.model.getFieldDefaults
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toProfilePickerItem
import com.wordco.clockworkandroid.edit_session_feature.ui.util.toEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.util.updateIfRetrieved
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

class SessionFormViewModel(
    formMode: SessionFormMode,
    private val profileRepository: ProfileRepository,
    private val getSessionUseCase: GetSessionUseCase,
    private val insertNewSessionUseCase: InsertNewSessionUseCase,
    private val updateSessionUseCase: UpdateSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SessionFormUiState>(SessionFormUiState.Retrieving(
        when(formMode) {
            is SessionFormMode.Create -> "Create New Session"
            is SessionFormMode.Edit -> "Edit Session"
        }
    ))
    val uiState: StateFlow<SessionFormUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<SessionFormEffect>()
    val effect = _effect.asSharedFlow()


    private sealed interface InternalState {
        data object Create : InternalState
        data class Edit(val session: Task) : InternalState
    }

    private lateinit var internalState: InternalState

    private lateinit var profiles: StateFlow<List<Profile>>

    private lateinit var fieldDefaults: SessionFormDefaults

    private var profileId: Long? = null

    init {
        viewModelScope.launch {
            profiles = profileRepository.getProfiles().run {
                stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(),
                    first()
                )
            }

            val profilesList = profiles.value

            when (formMode) {
                is SessionFormMode.Create -> {
                    profileId = formMode.profileId
                    internalState = InternalState.Create
                }
                is SessionFormMode.Edit -> {
                    val session = getSessionUseCase(formMode.sessionId).first()
                    profileId = session.profileId
                    internalState = InternalState.Edit(session)
                }
            }

            fieldDefaults = getFieldDefaults(
                profileId?.let { id -> profilesList.first { it.id == id } }
            )


            _uiState.update {
                when (val state = internalState) {
                    is InternalState.Create -> {
                        val initialPage = if (profileId == null && profilesList.isNotEmpty()) 0 else 1
                        SessionFormUiState.Retrieved(
                            title = "Create New Session",
                            profiles = profilesList.map { it.toProfilePickerItem() },
                            initialPage = initialPage,
                            taskName = fieldDefaults.taskName,
                            profileName = fieldDefaults.profileName,
                            colorSliderPos = fieldDefaults.colorSliderPos,
                            difficulty = fieldDefaults.difficulty,
                            dueDate = null,
                            dueTime = fieldDefaults.dueTime,
                            estimate = fieldDefaults.estimate,
                            hasFieldChanges = false,
                            isEstimateEditable = true,
                        )
                    }
                    is InternalState.Edit -> {
                        val session = state.session
                        SessionFormUiState.Retrieved(
                            title = "Edit Session",
                            initialPage = 1,
                            profiles = profilesList.map { it.toProfilePickerItem() },
                            taskName = session.name,
                            profileName = fieldDefaults.profileName,
                            colorSliderPos = session.color.hue() / 360,
                            difficulty = session.difficulty.toFloat(),
                            dueDate = session.dueDate?.atZone(ZoneId.systemDefault())
                                ?.toLocalDate(),
                            dueTime = session.dueDate?.run {
                                atZone(ZoneId.systemDefault())?.toLocalTime()
                            } ?: fieldDefaults.dueTime,
                            estimate = session.userEstimate?.toEstimate(),
                            isEstimateEditable = session is NewTask,
                            hasFieldChanges = false,
                        )
                    }
                }
            }

            profiles.collect { profiles ->
                _uiState.updateIfRetrieved { uiState ->
                    uiState.copy(profiles = profiles.map { it.toProfilePickerItem() })
                }
            }
        }
    }

    fun onEvent(event: SessionFormEvent) {
        when(event) {
            is SessionFormEvent.ColorSliderChanged -> onColorSliderChange(event.position)
            is SessionFormEvent.DifficultyChanged -> onDifficultyChange(event.difficulty)
            is SessionFormEvent.DueDateChanged -> onDueDateChange(event.date)
            is SessionFormEvent.DueTimeChanged -> onDueTimeChange(event.time)
            is SessionFormEvent.EstimateChanged -> onEstimateChange(event.estimate)
            is SessionFormEvent.ProfileChanged -> onProfileChange(event.id)
            is SessionFormEvent.SaveClicked -> onSaveClick()
            is SessionFormEvent.TaskNameChanged -> onTaskNameChange(event.name)
        }
    }


    private fun onTaskNameChange(newName: String) {
        _uiState.updateIfRetrieved { it.copy(
            taskName = newName,
            hasFieldChanges = true,
        ) }
    }

    private fun onColorSliderChange(newPos: Float) {
        _uiState.updateIfRetrieved { it.copy(
            colorSliderPos = newPos,
            hasFieldChanges = true,
        ) }
    }

    private fun onDifficultyChange(newDifficulty: Float) {
        _uiState.updateIfRetrieved { it.copy(
            difficulty = newDifficulty,
            hasFieldChanges = true,
        ) }
    }

    private fun onDueDateChange(newDate: Long?) {
        _uiState.updateIfRetrieved { it.copy(dueDate = newDate?.let {
            Instant.ofEpochMilli(newDate)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
        },
            hasFieldChanges = true,
        ) }
    }

    private fun onDueTimeChange(newTime: LocalTime) {
        _uiState.updateIfRetrieved { it.copy(
            dueTime = newTime,
            hasFieldChanges = true,
        ) }
    }

    private fun onEstimateChange(newEstimate: UserEstimate?) {
        _uiState.updateIfRetrieved { it.copy(
            estimate = newEstimate,
            hasFieldChanges = true,
        ) }
    }

    private fun onProfileChange(newProfileId: Long?) {
        if (newProfileId == profileId) return

        _uiState.updateIfRetrieved { uiState ->

            profileId = newProfileId
            val oldDefaults = fieldDefaults
            fieldDefaults = getFieldDefaults(
                newProfileId?.let { profiles.value.first { it.id == newProfileId } }
            )
            val profileName = fieldDefaults.profileName

            when(internalState) {
                is InternalState.Create -> {
                    val taskName = if (uiState.taskName == oldDefaults.taskName) {
                        fieldDefaults.taskName
                    } else uiState.taskName

                    val colorSliderPos = if (uiState.colorSliderPos == oldDefaults.colorSliderPos) {
                        fieldDefaults.colorSliderPos
                    } else uiState.colorSliderPos

                    val difficulty = if (uiState.difficulty == oldDefaults.difficulty) {
                        fieldDefaults.difficulty
                    } else uiState.difficulty

                    uiState.copy(
                        profileName = profileName,
                        taskName = taskName,
                        colorSliderPos = colorSliderPos,
                        difficulty = difficulty,
                        hasFieldChanges = true,
                    )
                }
                is InternalState.Edit -> {
                    uiState.copy(
                        profileName = profileName,
                        hasFieldChanges = true,
                    )
                }
            }
        }
    }

    private fun onSaveClick() {
        _uiState.getIfType<SessionFormUiState.Retrieved>()?.run {
            if (taskName.isBlank()) {
                viewModelScope.launch {
                    _effect.emit(SessionFormEffect.ShowSnackbar(
                        "Failed to save session: Missing Name"
                    ))
                }
                return
            }

            viewModelScope.launch {
                when (val state = internalState) {
                    is InternalState.Create -> {
                        val task = buildSession(this@run) as NewTask
                        insertNewSessionUseCase(task)
                    }
                    is InternalState.Edit -> {
                        val newTask = buildSession(this@run)
                        val oldSession = state.session
                        updateSessionUseCase(newTask, oldSession)
                    }
                }

                _uiState.updateIfRetrieved { it.copy(hasFieldChanges = false) }

                _effect.emit(SessionFormEffect.NavigateBack)
            }
        } ?: error("Can only save if retrieved")
    }

    private fun buildSession(state: SessionFormUiState.Retrieved): Task {
        val name = state.taskName
        val dueDate = state.dueDate?.let {
            LocalDateTime.of(it, state.dueTime).atZone(ZoneId.systemDefault()).toInstant()
        }
        val difficulty = state.difficulty.toInt()
        val color = Color.hsv(state.colorSliderPos * 360, 1f, 1f)
        val userEstimate = state.estimate?.toDuration()

        return when (val state = internalState) {
            is InternalState.Create -> NewTask(
                taskId = 0, name, dueDate, difficulty, color,
                userEstimate, profileId = profileId, appEstimate = null
            )

            is InternalState.Edit -> {
                val oldSession = state.session
                when (oldSession) {
                    is NewTask -> NewTask(
                        oldSession.taskId, name, dueDate, difficulty, color,
                        userEstimate, profileId, oldSession.appEstimate
                    )

                    is StartedTask -> StartedTask(
                        oldSession.taskId, name, dueDate, difficulty, color,
                        userEstimate, oldSession.segments, oldSession.markers,
                        profileId, oldSession.appEstimate
                    )

                    is CompletedTask -> CompletedTask(
                        oldSession.taskId, name, dueDate, difficulty, color,
                        userEstimate, oldSession.segments, oldSession.markers,
                        profileId, oldSession.appEstimate
                    )
                }
            }
        }
    }


    companion object {
        val FORM_MODE_KEY = object : CreationExtras.Key<SessionFormMode> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer

                val profileRepository = appContainer.profileRepository

                val formMode = this[FORM_MODE_KEY] as SessionFormMode

                val getSessionUseCase = appContainer.getSessionUseCase
                val insertNewSessionUseCase = appContainer.insertNewSessionUseCase
                val updateSessionUseCase = appContainer.updateSessionUseCase

                SessionFormViewModel(
                    profileRepository = profileRepository,
                    formMode = formMode,
                    getSessionUseCase = getSessionUseCase,
                    insertNewSessionUseCase = insertNewSessionUseCase,
                    updateSessionUseCase = updateSessionUseCase,
                )
            }
        }
    }
}