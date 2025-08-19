package com.wordco.clockworkandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date


data class NewTaskUiState (
    val taskName: String,
    val colorSliderPos: Float,
    val difficulty: Float,
    val dueDate: LocalDate?,
    val dueTime: LocalTime,
    val currentModal: NewTaskViewModel.PickerModal?,
    val estimate: NewTaskViewModel.EstimationComponents?
)


class NewTaskViewModel (
    taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NewTaskUiState(
            taskName = "",
            colorSliderPos = 0f,
            difficulty = 0f,
            dueDate = null,
            dueTime = LocalTime.MIDNIGHT,
            currentModal = null,
            estimate = null
        )
    )

    val uiState: StateFlow<NewTaskUiState> = _uiState.asStateFlow()

    data class EstimationComponents (
        val minutes: Int,
        val hours: Int,
    )

    enum class PickerModal {
        DATE, TIME
    }


    fun onTaskNameChange(newName: String) {

    }

    fun onColorSliderChange(newPos: Float) {

    }

    fun onDifficultyChange(newDifficulty: Float) {

    }

    fun onShowDatePicker() {

    }

    fun onDismissDatePicker() {

    }

    fun onDueDateChange(newDate: Long?) {

    }

    fun onShowTimePicker() {

    }

    fun onDismissTimePicker() {

    }

    fun onDueTimeChange(newTime: LocalTime) {

    }

    fun onEstimateChange(newEstimate: EstimationComponents) {

    }

    fun onCreateTaskClick() {

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