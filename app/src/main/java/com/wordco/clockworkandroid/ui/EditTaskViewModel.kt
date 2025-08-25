package com.wordco.clockworkandroid.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.domain.model.ExecutionStatus
import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset


private fun Color.hue() : Float {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    return hsl[0]
}


data class EditTaskUiState (
    override val taskName: String = "N/A",
    override val colorSliderPos: Float = 0f,
    override val difficulty: Float = 0f,
    override val dueDate: LocalDate? = LocalDate.now(),
    override val dueTime: LocalTime? = LocalTime.now(),
    override val currentModal: PickerModal? = null,
    override val estimate: UserEstimate? = UserEstimate(15,0)
) : EditTaskFormUiState


class EditTaskViewModel (
    private val taskRepository: TaskRepository,
    private val taskId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow<EditTaskUiState>(EditTaskUiState())
    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()
    private lateinit var _loadedTask: Task
    init {
        viewModelScope.launch {
            _loadedTask = taskRepository.getTask(taskId).first()
            with (_loadedTask) {
                _uiState.update {
                    EditTaskUiState(
                        taskName = name,
                        colorSliderPos = color.hue()/360,
                        difficulty = difficulty.toFloat(),
                        dueDate = dueDate?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                        dueTime = dueDate?.atZone(ZoneId.systemDefault())?.toLocalTime(),
                        currentModal = null,
                        estimate = UserEstimate(
                            minutes = 15,
                            hours = 0
                        )
                    )
                }
            }
        }
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

    sealed interface EditTaskResult {
        data object Success : EditTaskResult
        sealed interface Error : EditTaskResult
        data object MissingName : Error
    }

    fun onEditTaskClick() : EditTaskResult {
        with(_uiState.value) {

            if (taskName.isBlank()) {
                return EditTaskResult.MissingName
            }

            viewModelScope.launch {
                taskRepository.updateTask(
                    Task(
                        taskId = taskId,
                        name = taskName,
                        // 2007-12-03T10:15:30.00Z
                        dueDate = dueDate?.let {
                            LocalDateTime.of(it, dueTime)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                        },
                        difficulty = difficulty.toInt(),
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

        return EditTaskResult.Success
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