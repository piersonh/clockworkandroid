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
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.ui.util.fromSlider
import com.wordco.clockworkandroid.core.ui.util.getIfType
import com.wordco.clockworkandroid.core.ui.util.hue
import com.wordco.clockworkandroid.edit_session_feature.ui.model.CreateTaskResult
import com.wordco.clockworkandroid.edit_session_feature.ui.model.PickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toProfilePickerItem
import com.wordco.clockworkandroid.edit_session_feature.ui.util.updateIfRetrieved
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.random.Random

private val FIELD_DEFAULTS = object : EditTaskFormUiState {
    override val taskName: String = ""
    override val profileName: String? = null
    override val colorSliderPos: Float
        get() {
            return Random.nextFloat()
        }
    override val difficulty: Float = 0f
    override val dueDate: LocalDate? = null
    override val dueTime: LocalTime? = LocalTime.MIDNIGHT
    override val currentModal: PickerModal? = null
    override val estimate: UserEstimate? = UserEstimate(
        minutes = 15,
        hours = 0
    )
}



class CreateNewTaskViewModel (
    private val taskRepository: TaskRepository,
    private val profileRepository: ProfileRepository,
    private var profileId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditTaskUiState>(EditTaskUiState.Retrieving)

    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()

    private lateinit var _profiles: StateFlow<List<Profile>>

    init {
        viewModelScope.launch {
            // Set defaults when done loading
            _profiles = profileRepository.getProfiles().run {
                stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(),
                    first().also { profiles ->
                        val templateProfile = profileId?.let{ id -> profiles.first { it.id == id } }

                        val taskName = templateProfile?.name ?: FIELD_DEFAULTS.taskName
                        val profileName = templateProfile?.name
                        val colorSliderPos =
                            templateProfile?.color?.hue()?.div(360) ?: FIELD_DEFAULTS.colorSliderPos
                        val difficulty = templateProfile?.defaultDifficulty ?: FIELD_DEFAULTS.difficulty

                        _uiState.update {
                            EditTaskUiState.Retrieved(
                                profiles = profiles.map { it.toProfilePickerItem() },
                                taskName = taskName,
                                profileName = profileName,
                                colorSliderPos = colorSliderPos,
                                difficulty = difficulty.toFloat(),
                                dueDate = FIELD_DEFAULTS.dueDate,
                                dueTime = FIELD_DEFAULTS.dueTime,
                                currentModal = FIELD_DEFAULTS.currentModal,
                                estimate = FIELD_DEFAULTS.estimate,
                            )
                        }
                    }
                )
            }

            // Watch for updates to profiles (probably won't happen)
            _profiles.collect { profiles ->
                _uiState.updateIfRetrieved { uiState ->
                    uiState.copy(profiles = profiles.map { it.toProfilePickerItem() })
                }
            }
        }
    }


    fun onTaskNameChange(newName: String) {
        _uiState.updateIfRetrieved { it.copy(taskName = newName) }
    }

    fun onProfileChange(newProfileId: Long?) {
        if (newProfileId == profileId) {
            return
        }

        _uiState.updateIfRetrieved { uiState ->
            val templateProfile = newProfileId?.let{ id -> _profiles.value.first { it.id == id } }
            val oldProfile = profileId?.let{ id -> _profiles.value.first { it.id == id } }

            val profileName = templateProfile?.name

            val taskName = if (uiState.taskName == (oldProfile?.name ?: FIELD_DEFAULTS.taskName)) {
                templateProfile?.name ?: FIELD_DEFAULTS.taskName
            } else uiState.taskName

            val colorSliderPos = if (uiState.colorSliderPos == (oldProfile?.color?.hue()?.div(360)
                    ?: FIELD_DEFAULTS.colorSliderPos)
            ) {
                templateProfile?.color?.hue()?.div(360) ?: FIELD_DEFAULTS.colorSliderPos
            } else uiState.colorSliderPos

            val difficulty = if (uiState.difficulty == (oldProfile?.defaultDifficulty
                    ?: FIELD_DEFAULTS.difficulty)
            ) {
                templateProfile?.defaultDifficulty ?: FIELD_DEFAULTS.difficulty
            } else uiState.difficulty

            uiState.copy(
                profileName = profileName,
                taskName = taskName,
                colorSliderPos = colorSliderPos,
                difficulty = difficulty.toFloat(),
            )
        }
    }

    fun onColorSliderChange(newPos: Float) {
        _uiState.updateIfRetrieved { it.copy(colorSliderPos = newPos) }
    }

    fun onDifficultyChange(newDifficulty: Float) {
        _uiState.updateIfRetrieved { it.copy(difficulty = newDifficulty) }
    }

    fun onShowDatePicker() {
        _uiState.updateIfRetrieved { it.copy(currentModal = PickerModal.DATE) }
    }

    fun onDismissDatePicker() {
        _uiState.updateIfRetrieved { it.copy(currentModal = null) }
    }

    fun onDueDateChange(newDate: Long?) {
        _uiState.updateIfRetrieved { it.copy(dueDate = newDate?.let {
            Instant.ofEpochMilli(newDate)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
        }) }
    }

    fun onShowTimePicker() {
        _uiState.updateIfRetrieved { it.copy(currentModal = PickerModal.TIME) }
    }

    fun onDismissTimePicker() {
        _uiState.updateIfRetrieved { it.copy(currentModal = null) }
    }

    fun onDueTimeChange(newTime: LocalTime) {
        _uiState.updateIfRetrieved { it.copy(dueTime = newTime) }
    }

    fun onEstimateChange(newEstimate: UserEstimate) {
        _uiState.updateIfRetrieved { it.copy(estimate = newEstimate) }
    }

    fun onCreateTaskClick() : CreateTaskResult {
        return _uiState.getIfType<EditTaskUiState.Retrieved>()?.run {
            if (taskName.isBlank()) {
                return CreateTaskResult.MissingName
            }

            viewModelScope.launch {
                taskRepository.insertNewTask(
                    NewTask(
                        taskId = 0,
                        profileId = null,
                        name = taskName,
                        // 2007-12-03T10:15:30.00Z
                        dueDate = dueDate?.let {
                            LocalDateTime.of(it, dueTime)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                        },
                        difficulty = difficulty.toInt(),
                        color = Color.fromSlider(colorSliderPos),
                        userEstimate = estimate?.toDuration(),
                    )
                )
            }
            CreateTaskResult.Success
        } ?: error("Can only save if retrieved")
    }


    companion object {

        val PROFILE_ID_KEY = object : CreationExtras.Key<Long?> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val taskRepository = (this[APPLICATION_KEY] as MainApplication).taskRepository
                val profileRepository = (this[APPLICATION_KEY] as MainApplication).profileRepository
                val withProfile = this[PROFILE_ID_KEY]

                CreateNewTaskViewModel(
                    taskRepository = taskRepository,
                    profileRepository = profileRepository,
                    profileId = withProfile,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}