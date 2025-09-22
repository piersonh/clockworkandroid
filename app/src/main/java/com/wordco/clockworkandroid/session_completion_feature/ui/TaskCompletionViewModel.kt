package com.wordco.clockworkandroid.session_completion_feature.ui

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
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.edit_session_feature.ui.model.toEstimate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate

//FIXME
class TaskCompletionViewModel (
    private val taskRepository: TaskRepository,
    private val taskId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow<TaskCompletionUiState>(TaskCompletionUiState.Retrieving)
    val uiState: StateFlow<TaskCompletionUiState> = _uiState.asStateFlow()
    private lateinit var _loadedTask: CompletedTask
    private val _internalState = MutableStateFlow(TaskCompletionUiState.Retrieved(
        name = "",
        dueDate = null,
        difficulty = 0f,
        color = Color.Red,
        estimate = null,
        segments = listOf(),
        markers = listOf(),
        workTime = Duration.ZERO,
        breakTime = Duration.ZERO,
        totalTime = Duration.ZERO
    ))

    init {
        viewModelScope.launch {
            _loadedTask = taskRepository.getTask(taskId).first() as CompletedTask
            with(_loadedTask) {
                _internalState.update {
                    TaskCompletionUiState.Retrieved(
                        name = name,
                        dueDate = LocalDate.now(),
                        difficulty = difficulty.toFloat(),
                        color = color,
                        estimate = userEstimate?.toEstimate(),
                        segments = segments,
                        markers = markers,
                        workTime = this.workTime,
                        breakTime = this.breakTime,
                        totalTime = this.workTime.plus(this.breakTime)
                    )
                }
            }

            _internalState.collect { state ->
                _uiState.update { state }
            }
        }
    }

    //TODO: Add methods


    companion object {
        val TASK_ID_KEY = object : CreationExtras.Key<Long> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                val taskRepository = (this[APPLICATION_KEY] as MainApplication).taskRepository
                val taskId = this[TASK_ID_KEY] as Long

                TaskCompletionViewModel(
                    taskRepository = taskRepository,
                    taskId
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}