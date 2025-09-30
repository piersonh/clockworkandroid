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
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.ui.util.Fallible
import com.wordco.clockworkandroid.core.ui.util.getIfType
import com.wordco.clockworkandroid.core.ui.util.hue
import com.wordco.clockworkandroid.edit_session_feature.ui.model.DeleteSessionError
import com.wordco.clockworkandroid.edit_session_feature.ui.model.PickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.SaveSessionError
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toProfilePickerItem
import com.wordco.clockworkandroid.edit_session_feature.ui.util.getFieldDefaults
import com.wordco.clockworkandroid.edit_session_feature.ui.util.toEstimate
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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset


class EditTaskViewModel (
    private val taskRepository: TaskRepository,
    private val profileRepository: ProfileRepository,
    private val taskId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow<EditTaskUiState>(EditTaskUiState.Retrieving)
    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()
    private lateinit var _loadedTask: Task
    private var _profileId: Long? = null

    private lateinit var _profiles: StateFlow<List<Profile>>

    private lateinit var _fieldDefaults: EditTaskFormUiState


    init {
        viewModelScope.launch {
            _loadedTask = taskRepository.getTask(taskId).first()

            // Set defaults when done loading
            _profiles = profileRepository.getProfiles().run {
                stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(),
                    first().also { profiles ->
                        _fieldDefaults = getFieldDefaults(
                            _loadedTask.profileId?.let {
                                id -> profiles.first { it.id == id }
                            }
                        )

                        _profileId = _loadedTask.profileId

                        _uiState.update {
                            EditTaskUiState.Retrieved(
                                profiles = profiles.map { it.toProfilePickerItem() },
                                taskName = _loadedTask.name,
                                profileName = _fieldDefaults.profileName,
                                colorSliderPos = _loadedTask.color.hue() / 360,
                                difficulty = _loadedTask.difficulty.toFloat(),
                                dueDate = _loadedTask.dueDate?.atZone(ZoneId.systemDefault())
                                    ?.toLocalDate(),
                                dueTime = _loadedTask.dueDate?.run {
                                    atZone(ZoneId.systemDefault())?.toLocalTime()
                                } ?: _fieldDefaults.dueTime,
                                currentModal = _fieldDefaults.currentModal,
                                estimate = _loadedTask.userEstimate?.toEstimate(),
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
        if (newProfileId == _profileId) {
            return
        }

        _uiState.updateIfRetrieved { uiState ->
            _profileId = newProfileId

            val profileName = getFieldDefaults(
               newProfileId?.let{ _profiles.value.first { it.id == newProfileId } }
            ).profileName

            uiState.copy(
                profileName = profileName,
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

    fun onEstimateChange(newEstimate: UserEstimate?) {
        _uiState.updateIfRetrieved { it.copy(estimate = newEstimate) }
    }

    fun onShowEstimatePicker() {
        _uiState.updateIfRetrieved { it.copy(currentModal = PickerModal.ESTIMATE) }
    }

    fun onDismissEstimatePicker() {
        _uiState.updateIfRetrieved { it.copy(currentModal = null) }
    }

    fun onEditTaskClick() : Fallible<SaveSessionError> {
        return _uiState.getIfType<EditTaskUiState.Retrieved>()?.run {
            if (taskName.isBlank()) {
                return Fallible.Error(SaveSessionError.MISSING_NAME)
            }

            val name = taskName

            val dueDate = dueDate?.let {
                LocalDateTime.of(it, dueTime)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            }

            val difficulty = difficulty.toInt()

            val color = Color.hsv(
                colorSliderPos * 360,
                1f,
                1f
            )

            val userEstimate = estimate?.toDuration()

            viewModelScope.launch {
                taskRepository.updateTask(
                    when (_loadedTask) {
                        is CompletedTask -> CompletedTask(
                            taskId,
                            name,
                            dueDate,
                            difficulty,
                            color,
                            userEstimate,
                            segments = (_loadedTask as CompletedTask).segments,
                            markers = (_loadedTask as CompletedTask).markers,
                            profileId = _profileId,
                        )
                        is NewTask -> NewTask(
                            taskId,
                            name,
                            dueDate,
                            difficulty,
                            color,
                            userEstimate,
                            profileId = _profileId,
                        )
                        is StartedTask -> StartedTask(
                            taskId,
                            name,
                            dueDate,
                            difficulty,
                            color,
                            userEstimate,
                            segments = (_loadedTask as StartedTask).segments,
                            markers = (_loadedTask as StartedTask).markers,
                            profileId = _profileId,
                        )
                    }

                )
            }
            Fallible.Success
        } ?: error("Can only save if retrieved")
    }
    fun onDeleteClick() : Fallible<DeleteSessionError> {
        return _uiState.getIfType<EditTaskUiState.Retrieved>()?.run {
            if (taskName.isBlank()) {
                return Fallible.Error(DeleteSessionError.MISSING_NAME)
            }

            viewModelScope.launch {
                taskRepository.deleteTask(_loadedTask)
            }
            Fallible.Success
        } ?: error("Can only delete if retrieved")
    }

    companion object {

        val TASK_ID_KEY = object : CreationExtras.Key<Long> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                val taskRepository = (this[APPLICATION_KEY] as MainApplication).taskRepository
                val profileRepository = (this[APPLICATION_KEY] as MainApplication).profileRepository
                val taskId = this[TASK_ID_KEY] as Long

                EditTaskViewModel (
                    taskRepository = taskRepository,
                    profileRepository = profileRepository,
                    taskId = taskId,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}