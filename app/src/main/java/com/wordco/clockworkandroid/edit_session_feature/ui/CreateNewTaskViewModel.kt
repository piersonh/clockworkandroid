package com.wordco.clockworkandroid.edit_session_feature.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.ui.fromSlider
import com.wordco.clockworkandroid.edit_session_feature.ui.model.PickerModal
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.random.Random


class CreateNewTaskViewModel (
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CreateNewTaskUiState(
            taskName = "",
            colorSliderPos = Random.nextFloat(),
            difficulty = 0f,
            dueDate = null,
            dueTime = LocalTime.MIDNIGHT,
            currentModal = null,
            estimate = UserEstimate(
                minutes = 15,
                hours = 0
            )
        )
    )

    val uiState: StateFlow<CreateNewTaskUiState> = _uiState.asStateFlow()


    fun onTaskNameChange(newName: String) {
        _uiState.update { it.copy(taskName = newName) }
    }

    fun onColorSliderChange(newPos: Float) {
        _uiState.update { it.copy(colorSliderPos = newPos) }
    }

    fun onDifficultyChange(newDifficulty: Float) {
        _uiState.update { it.copy(difficulty = newDifficulty) }
    }

    fun onShowDatePicker() {
        _uiState.update { it.copy(currentModal = PickerModal.DATE) }
    }

    fun onDismissDatePicker() {
        _uiState.update { it.copy(currentModal = null) }
    }

    fun onDueDateChange(newDate: Long?) {
        _uiState.update { it.copy(dueDate = newDate?.let {
            Instant.ofEpochMilli(newDate)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
        }) }
    }

    fun onShowTimePicker() {
        _uiState.update { it.copy(currentModal = PickerModal.TIME) }
    }

    fun onDismissTimePicker() {
        _uiState.update { it.copy(currentModal = null) }
    }

    fun onDueTimeChange(newTime: LocalTime) {
        _uiState.update { it.copy(dueTime = newTime) }
    }

    fun onEstimateChange(newEstimate: UserEstimate) {
        _uiState.update { it.copy(estimate = newEstimate) }
    }

    sealed interface CreateTaskResult {
        data object Success : CreateTaskResult
        sealed interface Error : CreateTaskResult
        data object MissingName : Error
    }

    fun onCreateTaskClick() : CreateTaskResult {
        with(_uiState.value) {

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
                        difficulty = (4 * difficulty).toInt(),
                        color = Color.fromSlider(colorSliderPos),
                        userEstimate = estimate?.toDuration(),
                    )
                )
            }
        }

        return CreateTaskResult.Success
    }


    companion object {


        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val taskRepository = (this[APPLICATION_KEY] as MainApplication).taskRepository

                CreateNewTaskViewModel (
                    taskRepository = taskRepository,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}