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
import com.wordco.clockworkandroid.core.domain.use_case.CalculateEstimateAccuracyUseCase
import com.wordco.clockworkandroid.core.domain.use_case.DeleteSessionUseCase
import com.wordco.clockworkandroid.core.domain.use_case.GetSessionUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TaskCompletionViewModel (
    private val taskId: Long,
    private val getSessionUseCase: GetSessionUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val calculateEstimateAccuracyUseCase: CalculateEstimateAccuracyUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<TaskCompletionUiState>(TaskCompletionUiState.Retrieving)
    val uiState: StateFlow<TaskCompletionUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TaskCompletionUiEvent>()
    val events = _events.asSharedFlow()

    private val loadedTask = getSessionUseCase(taskId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(),null)

    init {
        viewModelScope.launch {
            loadedTask.map { task ->
                if (task is CompletedTask) {
                    val totalTime = task.workTime.plus(task.breakTime)
                    TaskCompletionUiState.Retrieved(
                        name = task.name,
                        estimate = task.userEstimate,
                        workTime = task.workTime,
                        breakTime = task.breakTime,
                        totalTime = totalTime,
                        totalTimeAccuracy = task.userEstimate?.let{
                            calculateEstimateAccuracyUseCase(
                                totalTime,
                                task.userEstimate
                            )
                        },
                    )
                } else {
                    TaskCompletionUiState.Retrieving
                }
            }.collect { state ->
                _uiState.update { state }
            }
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            deleteSessionUseCase(taskId)
            _events.emit(TaskCompletionUiEvent.NavigateBack)
        }
    }

    companion object {
        val TASK_ID_KEY = object : CreationExtras.Key<Long> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer
                val getSessionUseCase = appContainer.getSessionUseCase
                val calculateEstimateAccuracyUseCase = appContainer.calculateEstimateAccuracyUseCase
                val deleteSessionUseCase = appContainer.deleteSessionUseCase
                val taskId = this[TASK_ID_KEY] as Long

                TaskCompletionViewModel(
                    taskId,
                    getSessionUseCase = getSessionUseCase,
                    calculateEstimateAccuracyUseCase = calculateEstimateAccuracyUseCase,
                    deleteSessionUseCase = deleteSessionUseCase,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}