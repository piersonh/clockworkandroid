package com.wordco.clockworkandroid.session_completion_feature.ui

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
import com.wordco.clockworkandroid.session_completion_feature.domain.use_case.CalculateEstimateAccuracyUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TaskCompletionViewModel (
    private val taskRepository: TaskRepository,
    private val taskId: Long,
    private val calculateEstimateAccuracyUseCase: CalculateEstimateAccuracyUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<TaskCompletionUiState>(TaskCompletionUiState.Retrieving)
    val uiState: StateFlow<TaskCompletionUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TaskCompletionUiEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            taskRepository
                .getTask(taskId)
                .first { it is CompletedTask }
                .let {it as CompletedTask }
                .run {
                    val totalTime = workTime.plus(breakTime)
                    TaskCompletionUiState.Retrieved(
                        name = name,
                        estimate = userEstimate,
                        workTime = workTime,
                        breakTime = breakTime,
                        totalTime = totalTime,
                        totalTimeAccuracy = userEstimate?.let{
                            calculateEstimateAccuracyUseCase(
                                totalTime,
                                userEstimate
                            )
                        },
                    )
                }.let { state ->
                    _uiState.update { state }
                }
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
            _events.emit(TaskCompletionUiEvent.NavigateBack)
        }
    }

    companion object {
        val TASK_ID_KEY = object : CreationExtras.Key<Long> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer
                val taskRepository = appContainer.sessionRepository
                val taskId = this[TASK_ID_KEY] as Long

                TaskCompletionViewModel(
                    taskRepository = taskRepository,
                    taskId,
                    calculateEstimateAccuracyUseCase = CalculateEstimateAccuracyUseCase()
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}