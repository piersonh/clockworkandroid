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
import com.wordco.clockworkandroid.edit_session_feature.ui.util.toEstimate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TaskCompletionViewModel (
    private val taskRepository: TaskRepository,
    private val taskId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow<TaskCompletionUiState>(TaskCompletionUiState.Retrieving)
    val uiState: StateFlow<TaskCompletionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            taskRepository
                .getTask(taskId)
                .first { it is CompletedTask }
                .let {it as CompletedTask }
                .run {
                    TaskCompletionUiState.Retrieved(
                        name = name,
                        estimate = userEstimate?.toEstimate(),
                        workTime = workTime,
                        breakTime = breakTime,
                        totalTime = workTime.plus(breakTime)
                    )
                }.let { state ->
                    _uiState.update { state }
                }
        }
    }

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