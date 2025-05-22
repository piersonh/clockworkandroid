package com.wordco.clockworkandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordco.clockworkandroid.data.local.TaskDao
import com.wordco.clockworkandroid.data.model.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {

    val allEntries: StateFlow<List<Task>> =
        taskDao.getAllTasks()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList<Task>()
            )

    fun insertTask(task: Task) {
        viewModelScope.launch {
            taskDao.insertTask(task)
        }
    }

    fun insertTasks(vararg tasks: Task) {
        viewModelScope.launch {
            for (task in tasks) {
                taskDao.insertTask(task)
            }
        }
    }
}