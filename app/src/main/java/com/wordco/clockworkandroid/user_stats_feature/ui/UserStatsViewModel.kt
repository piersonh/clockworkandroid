package com.wordco.clockworkandroid.user_stats_feature.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.user_stats_feature.ui.model.mapper.toCompletedSessionListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class UserStatsViewModel(
    private val taskRepository: TaskRepository,
) : ViewModel() {


    private val _uiState = MutableStateFlow<UserStatsUiState>(UserStatsUiState.Retrieving)

    val uiState: StateFlow<UserStatsUiState> = _uiState.asStateFlow()


    // TODO: make a getCompletedTasks (?)
    private val _tasks = taskRepository.getCompletedTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(),null)

    init {
        viewModelScope.launch {
            _tasks.map { tasks ->
                if (tasks == null) {
                    UserStatsUiState.Retrieving
                } else {
                    UserStatsUiState.Retrieved(
                        completedTasks = tasks
                            .map { it.toCompletedSessionListItem() }
                            .sortedBy { it.completedAt }
                            .reversed()
                    )
                }
            }.collect { uiState ->
                _uiState.update { uiState }
            }
        }
    }



    companion object {


        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val taskRepository = (this[APPLICATION_KEY] as MainApplication).taskRepository

                UserStatsViewModel (
                    taskRepository = taskRepository,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}