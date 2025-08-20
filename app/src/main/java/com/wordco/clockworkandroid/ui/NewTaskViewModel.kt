package com.wordco.clockworkandroid.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.domain.model.ExecutionStatus
import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.random.Random


data class NewTaskUiState (
    val taskName: String,
    val colorSliderPos: Float,
    val difficulty: Float,
    val dueDate: LocalDate?,
    val dueTime: LocalTime,
    val currentModal: NewTaskViewModel.PickerModal?,
    val estimate: NewTaskViewModel.UserEstimate?
)


class NewTaskViewModel (
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NewTaskUiState(
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

    val uiState: StateFlow<NewTaskUiState> = _uiState.asStateFlow()

    data class UserEstimate (
        val minutes: Int,
        val hours: Int,
    )

    enum class PickerModal {
        DATE, TIME
    }


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
                    Task(
                        taskId = 0,
                        name = taskName,
                        // 2007-12-03T10:15:30.00Z
                        dueDate = dueDate?.let {
                            LocalDateTime.of(it, dueTime)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                        },
                        difficulty = (4 * difficulty).toInt(),
                        color = Color.hsv(
                            colorSliderPos * 360,
                            1f,
                            1f),
                        status = ExecutionStatus.NOT_STARTED,
                        segments = listOf(),
                        markers = listOf(),
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

                NewTaskViewModel (
                    taskRepository = taskRepository,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}