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
import com.wordco.clockworkandroid.core.ui.util.getIfType
import com.wordco.clockworkandroid.core.ui.util.hue
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetAppEstimateUseCase
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toProfilePickerItem
import com.wordco.clockworkandroid.edit_session_feature.ui.util.getFieldDefaults
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


class EditTaskViewModel (
    private val taskRepository: TaskRepository,
    private val profileRepository: ProfileRepository,
    private val taskId: Long,
    private var getAppEstimateUseCase: GetAppEstimateUseCase = GetAppEstimateUseCase(),
) : ViewModel() {
    private val _uiState = MutableStateFlow<EditTaskUiState>(EditTaskUiState.Retrieving)
    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()


    private lateinit var loadedTask: Task
    private var profileId: Long? = null

    private lateinit var profiles: StateFlow<List<Profile>>

    private lateinit var fieldDefaults: SessionFormUiState


    init {
        viewModelScope.launch {
            loadedTask = taskRepository.getTask(taskId).first()

            // Set defaults when done loading
            profiles = profileRepository.getProfiles().run {
                stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(),
                    first().also { profiles ->
                        fieldDefaults = getFieldDefaults(
                            loadedTask.profileId?.let {
                                id -> profiles.first { it.id == id }
                            }
                        )

                        profileId = loadedTask.profileId

                        _uiState.update {
                            EditTaskUiState.Retrieved(
                                profiles = profiles.map { it.toProfilePickerItem() },
                                taskName = loadedTask.name,
                                profileName = fieldDefaults.profileName,
                                colorSliderPos = loadedTask.color.hue() / 360,
                                difficulty = loadedTask.difficulty.toFloat(),
                                dueDate = loadedTask.dueDate?.atZone(ZoneId.systemDefault())
                                    ?.toLocalDate(),
                                dueTime = loadedTask.dueDate?.run {
                                    atZone(ZoneId.systemDefault())?.toLocalTime()
                                } ?: fieldDefaults.dueTime,
                                estimate = loadedTask.userEstimate?.toEstimate(),
                                isEstimateEditable = loadedTask is NewTask,
                                hasFieldChanges = false,
                            )
                        }
                    }
                )
            }

            // Watch for updates to profiles (probably won't happen)
            profiles.collect { profiles ->
                _uiState.updateIfRetrieved { uiState ->
                    uiState.copy(profiles = profiles.map { it.toProfilePickerItem() })
                }
            }
        }
    }


    fun onTaskNameChange(newName: String) {
        _uiState.updateIfRetrieved { it.copy(
            taskName = newName,
            hasFieldChanges = true,
        ) }
    }

    fun onProfileChange(newProfileId: Long?) {
        if (newProfileId == profileId) {
            return
        }

        _uiState.updateIfRetrieved { uiState ->
            profileId = newProfileId

            val profileName = getFieldDefaults(
               newProfileId?.let{ profiles.value.first { it.id == newProfileId } }
            ).profileName

            uiState.copy(
                profileName = profileName,
                hasFieldChanges = true,
            )
        }
    }


    fun onColorSliderChange(newPos: Float) {
        _uiState.updateIfRetrieved { it.copy(
            colorSliderPos = newPos,
            hasFieldChanges = true,
        ) }
    }

    fun onDifficultyChange(newDifficulty: Float) {
        _uiState.updateIfRetrieved { it.copy(
            difficulty = newDifficulty,
            hasFieldChanges = true,
        ) }
    }

    fun onDueDateChange(newDate: Long?) {
        _uiState.updateIfRetrieved { it.copy(dueDate = newDate?.let {
            Instant.ofEpochMilli(newDate)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
        },
            hasFieldChanges = true,
        ) }
    }

    fun onDueTimeChange(newTime: LocalTime) {
        _uiState.updateIfRetrieved { it.copy(
            dueTime = newTime,
            hasFieldChanges = true,
            ) }
    }

    fun onEstimateChange(newEstimate: UserEstimate?) {
        _uiState.updateIfRetrieved { it.copy(
            estimate = newEstimate,
            hasFieldChanges = true,
        ) }
    }


    fun onSaveClick() : Boolean {
        return _uiState.getIfType<EditTaskUiState.Retrieved>()?.run {
            if (taskName.isBlank()) {
                viewModelScope.launch {
                    _snackbarEvent.emit("Failed to save session: Missing Name")
                }
                return@run false
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

            val task = when (loadedTask) {
                is NewTask -> NewTask(
                    taskId,
                    name,
                    dueDate,
                    difficulty,
                    color,
                    userEstimate,
                    profileId = profileId,
                    appEstimate = loadedTask.appEstimate,
                )

                is StartedTask -> StartedTask(
                    taskId,
                    name,
                    dueDate,
                    difficulty,
                    color,
                    userEstimate,
                    segments = (loadedTask as StartedTask).segments,
                    markers = (loadedTask as StartedTask).markers,
                    profileId = profileId,
                    appEstimate = loadedTask.appEstimate,
                )
                is CompletedTask -> CompletedTask(
                    taskId,
                    name,
                    dueDate,
                    difficulty,
                    color,
                    userEstimate,
                    segments = (loadedTask as CompletedTask).segments,
                    markers = (loadedTask as CompletedTask).markers,
                    profileId = profileId,
                    appEstimate = loadedTask.appEstimate,
                )
            }

            val shouldRecalculateEstimate = (profileId != loadedTask.profileId)
                .or(difficulty != loadedTask.difficulty)
                .or(userEstimate?.equals(loadedTask.userEstimate)?.not() ?: false)

            viewModelScope.launch {
                if (shouldRecalculateEstimate && userEstimate != null) {
                    taskRepository.updateTask(
                        when(task) {
                            is NewTask -> {
                                val sessionHistory = taskRepository.getCompletedTasks().first()
                                    .filter { it.userEstimate != null }

                                val appEstimate = getAppEstimateUseCase(
                                    todoSession = task,
                                    sessionHistory = sessionHistory
                                )

                                task.copy(appEstimate = appEstimate)
                            }
                            is StartedTask,
                            is CompletedTask -> task
                        }
                    )
                } else {
                    taskRepository.updateTask(task)
                }

            }
            _uiState.updateIfRetrieved { it.copy(hasFieldChanges = false) }

            true
        } ?: error("Can only save if retrieved")
    }


    companion object {

        val TASK_ID_KEY = object : CreationExtras.Key<Long> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer
                val taskRepository = appContainer.sessionRepository
                val profileRepository = appContainer.profileRepository
                val taskId = this[TASK_ID_KEY] as Long

                EditTaskViewModel(
                    taskRepository = taskRepository,
                    profileRepository = profileRepository,
                    taskId = taskId,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}