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
import com.wordco.clockworkandroid.core.domain.use_case.GetAllProfilesUseCase
import com.wordco.clockworkandroid.core.domain.use_case.GetSessionUseCase
import com.wordco.clockworkandroid.core.ui.util.getIfType
import com.wordco.clockworkandroid.core.ui.util.hue
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.CreateSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetAverageEstimateErrorUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetAverageSessionDurationUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetRemindersForSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.UpdateSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.ui.model.ReminderListItem
import com.wordco.clockworkandroid.edit_session_feature.ui.model.SessionFormDefaults
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toProfilePickerItem
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toReminderListItem
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
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.random.Random

class SessionFormViewModel(
    formMode: SessionFormMode,
    private val getSessionUseCase: GetSessionUseCase,
    private val getAllProfilesUseCase: GetAllProfilesUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val updateSessionUseCase: UpdateSessionUseCase,
    private val getAverageSessionDurationUseCase: GetAverageSessionDurationUseCase,
    private val getAverageEstimateErrorUseCase: GetAverageEstimateErrorUseCase,
    private val getRemindersForSessionUseCase: GetRemindersForSessionUseCase,
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

    private var getAverageSessionDuration: ((Int) -> Duration)? = null

    private var getAverageEstimateError: ((Int) -> Double)? = null

    init {
        viewModelScope.launch {
            profiles = getAllProfilesUseCase().run {
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

            val profileId = profileId
            if (profileId != null) {
                getAverageSessionDuration = getAverageSessionDurationUseCase(
                    profileId = profileId,
                )

                getAverageEstimateError = getAverageEstimateErrorUseCase(
                    profileId = profileId,
                )
            }

            fieldDefaults = getFieldDefaults(
                profileId?.let { id -> profilesList.first { it.id == id } }
            )

            println(fieldDefaults.difficulty.toInt())

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
                            averageSessionDuration = getAverageSessionDuration?.invoke(
                                fieldDefaults.difficulty.toInt()
                            ),
                            averageEstimateError = getAverageEstimateError?.invoke(
                                fieldDefaults.difficulty.toInt()
                            ),
                            reminder = null
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
                            averageSessionDuration = getAverageSessionDuration?.invoke(
                                session.difficulty
                            ),
                            averageEstimateError = getAverageEstimateError?.invoke(
                                session.difficulty
                            ),
                            reminder = getRemindersForSessionUseCase(session.taskId)
                                .first().firstOrNull()?.toReminderListItem()
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
            is SessionFormEvent.ReminderDateChanged -> onReminderDateChange(event.date)
            is SessionFormEvent.ReminderTimeChanged -> onReminderTimeChange(event.time)
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
        _uiState.updateIfRetrieved {
            it.copy(
                difficulty = newDifficulty,
                hasFieldChanges = true,
                averageSessionDuration = getAverageSessionDuration?.invoke(
                    newDifficulty.toInt()
                ),
                averageEstimateError = getAverageEstimateError?.invoke(
                    newDifficulty.toInt()
                ),
            )
        }
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

        viewModelScope.launch {

            val oldDefaults = fieldDefaults
            profileId = newProfileId

            if (newProfileId != null) {
                getAverageSessionDuration = getAverageSessionDurationUseCase(
                    profileId = newProfileId,
                )

                getAverageEstimateError = getAverageEstimateErrorUseCase(
                    profileId = newProfileId,
                )
            } else {
                getAverageSessionDuration = null
                getAverageEstimateError = null
            }


            fieldDefaults = getFieldDefaults(
                newProfileId?.let { profiles.value.first { it.id == newProfileId } }
            )

            val profileName = fieldDefaults.profileName

            _uiState.updateIfRetrieved { uiState ->

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
                            averageSessionDuration = getAverageSessionDuration?.invoke(
                                difficulty.toInt()
                            ),
                            averageEstimateError = getAverageEstimateError?.invoke(
                                difficulty.toInt()
                            ),
                        )
                    }
                    is InternalState.Edit -> {
                        uiState.copy(
                            profileName = profileName,
                            hasFieldChanges = true,
                            averageSessionDuration = getAverageSessionDuration?.invoke(
                                uiState.difficulty.toInt()
                            ),
                            averageEstimateError = getAverageEstimateError?.invoke(
                                uiState.difficulty.toInt()
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun onReminderDateChange(newDate: Long?) {
        _uiState.updateIfRetrieved { uiState ->
            uiState.copy(
                reminder = newDate?.let {
                    val date = Instant.ofEpochMilli(newDate)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()
                    uiState.reminder?.copy(scheduledDate = date)
                        ?: ReminderListItem(
                            scheduledDate = date,
                            scheduledTime = fieldDefaults.reminderTime
                        )
                },
                hasFieldChanges = true,
            )
        }
    }

    private fun onReminderTimeChange(newTime: LocalTime) {
        _uiState.updateIfRetrieved { uiState ->
            uiState.copy(
                reminder = uiState.reminder?.copy(
                    scheduledTime = newTime
                ),
                hasFieldChanges = true,
            )
        }
    }

    private fun onSaveClick() {
        _uiState.getIfType<SessionFormUiState.Retrieved>()?.run {
            if (taskName.isBlank()) {
                viewModelScope.launch {
                    _effect.emit(SessionFormEffect.ShowSnackbar(
                        "Please give the session a name."
                    ))
                }
                return
            }

            viewModelScope.launch {
                when (internalState) {
                    is InternalState.Create -> {
                        val task = buildSession(this@run) as NewTask
                        createSessionUseCase(
                            task = task,
                            reminderTimes = listOfNotNull(
                                reminder?.run {
                                    LocalDateTime.of(scheduledDate, scheduledTime)
                                        .atZone(ZoneId.systemDefault()).toInstant()
                                }
                            )
                        )
                    }
                    is InternalState.Edit -> {
                        val newTask = buildSession(this@run)
                        updateSessionUseCase(
                            newTask,
                            reminderTimes = listOfNotNull(
                                reminder?.takeIf { newTask !is CompletedTask }?.run {
                                    LocalDateTime.of(scheduledDate, scheduledTime)
                                        .atZone(ZoneId.systemDefault()).toInstant()
                                }
                            ),
                        )
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

                val formMode = this[FORM_MODE_KEY] as SessionFormMode

                SessionFormViewModel(
                    formMode = formMode,
                    getSessionUseCase = appContainer.getSessionUseCase,
                    getAllProfilesUseCase = appContainer.getAllProfilesUseCase,
                    createSessionUseCase = appContainer.createSessionUseCase,
                    updateSessionUseCase = appContainer.updateSessionUseCase,
                    getAverageSessionDurationUseCase = appContainer.getAverageSessionDurationUseCase,
                    getAverageEstimateErrorUseCase = appContainer.getAverageEstimateErrorUseCase,
                    getRemindersForSessionUseCase = appContainer.getRemindersForSessionUseCase,
                )
            }
        }
    }
}