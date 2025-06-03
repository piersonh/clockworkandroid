package com.wordco.clockworkandroid.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordco.clockworkandroid.domain.model.Task
import com.wordco.clockworkandroid.domain.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel (
    private val taskRepository: TaskRepository
) : ViewModel() {

    private lateinit var tasks : List<Task>

    var taskList by mutableStateOf<List<Task>>(emptyList())
        private set

    init {
        viewModelScope.launch {
            tasks = taskRepository.getTasks()
        }
    }
}