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
import kotlin.collections.first
import kotlin.random.Random


class CreateNewTaskViewModel (
    private val taskRepository: TaskRepository,
    private val profileRepository: ProfileRepository,
    private var profileId: Long?
) : ViewModel() {

    private val _rng = Random(System.currentTimeMillis())

    private val _uiState = MutableStateFlow<EditTaskUiState>(EditTaskUiState.Retrieving)

    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()

    private lateinit var _profiles: StateFlow<List<Profile>>

    private lateinit var _fieldDefaults: EditTaskFormUiState

    init {
        viewModelScope.launch {
            // Set defaults when done loading
            _profiles = profileRepository.getProfiles().run {
                stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(),
                    first().also { profiles ->
                        _fieldDefaults = getDefaultFields(
                            profileId?.let{ id -> profiles.first { it.id == id } }
                        )

                        _uiState.update {
                            EditTaskUiState.Retrieved(
                                profiles = profiles.map { it.toProfilePickerItem() },
                                taskName = _fieldDefaults.taskName,
                                profileName = _fieldDefaults.profileName,
                                colorSliderPos = _fieldDefaults.colorSliderPos,
                                difficulty = _fieldDefaults.difficulty,
                                dueDate = _fieldDefaults.dueDate,
                                dueTime = _fieldDefaults.dueTime,
                                currentModal = _fieldDefaults.currentModal,
                                estimate = _fieldDefaults.estimate,
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

    private fun getDefaultFields(
        profile: Profile?
    ) = object : EditTaskFormUiState {
        override val taskName: String = profile?.let { profile ->
            // TODO replace this with a truth from the database so that the quantity
            //  is retained after sessions are deleted or switched profiles
            "${ profile.name} ${profile.sessions.size + 1}"
        } ?: ""
        override val profileName: String? = profile?.name
        override val colorSliderPos: Float = profile?.color?.hue()?.div(360) ?: _rng.nextFloat()
        override val difficulty: Float = profile?.defaultDifficulty?.toFloat() ?: 0f
        override val dueDate: LocalDate? = null
        override val dueTime: LocalTime? = LocalTime.MIDNIGHT
        override val currentModal: PickerModal? = null
        override val estimate: UserEstimate? = UserEstimate(
            minutes = 15,
            hours = 0
        )
    }


    fun onTaskNameChange(newName: String) {
        _uiState.updateIfRetrieved { it.copy(taskName = newName) }
    }

    fun onProfileChange(newProfileId: Long?) {
        if (newProfileId == profileId) {
            return
        }

        _uiState.updateIfRetrieved { uiState ->
            val oldDefaults = _fieldDefaults

            _fieldDefaults = getDefaultFields(
                profileId?.let{ id -> _profiles.value.first { it.id == newProfileId } }
            )

            val profileName = _fieldDefaults.profileName

            val taskName = if (uiState.taskName == oldDefaults.taskName) {
                _fieldDefaults.taskName
            } else uiState.taskName

            val colorSliderPos = if (uiState.colorSliderPos == oldDefaults.colorSliderPos) {
                _fieldDefaults.colorSliderPos
            } else uiState.colorSliderPos

            val difficulty = if (uiState.difficulty == oldDefaults.difficulty) {
                _fieldDefaults.difficulty
            } else uiState.difficulty

            uiState.copy(
                profileName = profileName,
                taskName = taskName,
                colorSliderPos = colorSliderPos,
                difficulty = difficulty,
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