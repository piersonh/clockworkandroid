package com.wordco.clockworkandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalTime
import java.util.Date


data class NewTaskUiState (
    val taskName: String,
    val colorSliderPos: Float,
    val difficulty: Float,
    val dueDate: Date?,
    val dueTime: LocalTime,
    val currentModal: NewTaskViewModel.PickerModal?
)


class NewTaskViewModel (
    taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewTaskUiState>()

    val uiState: StateFlow<NewTaskUiState> = _uiState.asStateFlow()

    enum class PickerModal {
        DATE, TIME
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