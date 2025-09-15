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
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.ui.util.getIfType
import com.wordco.clockworkandroid.core.ui.util.hue
import com.wordco.clockworkandroid.edit_session_feature.ui.model.EditTaskResult
import com.wordco.clockworkandroid.edit_session_feature.ui.model.PickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.util.toEstimate
import com.wordco.clockworkandroid.edit_session_feature.ui.util.updateIfRetrieved
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset


class EditTaskViewModel (
    private val taskRepository: TaskRepository,
    private val taskId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow<EditTaskUiState>(EditTaskUiState.Retrieving)
    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()
    private lateinit var _loadedTask: Task


    init {
        viewModelScope.launch {
            taskRepository.getTask(taskId).first().run {
                _loadedTask = this

                _uiState.update {
                    EditTaskUiState.Retrieved(
                        taskName = name,
                        colorSliderPos = color.hue() / 360,
                        difficulty = difficulty.toFloat(),
                        dueDate = dueDate?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                        dueTime = dueDate?.run {
                            atZone(ZoneId.systemDefault())?.toLocalTime()
                        } ?: LocalTime.MIDNIGHT,
                        currentModal = null,
                        estimate = userEstimate?.toEstimate(),
                        profileName = null, // TODO
                        profiles = emptyList() // TODO
                    )
                }
            }
        }
    }


    fun onTaskNameChange(newName: String) {
        _uiState.updateIfRetrieved { it.copy(taskName = newName) }
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

    fun onEditTaskClick() : EditTaskResult {
        return _uiState.getIfType<EditTaskUiState.Retrieved>()?.run {
            if (taskName.isBlank()) {
                return EditTaskResult.MissingName
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
                            _loadedTask.profileId,
                        )
                        is NewTask -> NewTask(
                            taskId,
                            name,
                            dueDate,
                            difficulty,
                            color,
                            userEstimate,
                            _loadedTask.profileId,
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
                            _loadedTask.profileId,
                        )
                    }

                )
            }
            EditTaskResult.Success
        } ?: error("Can only save if retrieved")
    }


    companion object {

        val TASK_ID_KEY = object : CreationExtras.Key<Long> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                val taskRepository = (this[APPLICATION_KEY] as MainApplication).taskRepository
                val taskId = this[TASK_ID_KEY] as Long

                EditTaskViewModel (
                    taskRepository = taskRepository,
                    taskId
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}