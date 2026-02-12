package com.wordco.clockworkandroid.session_list_feature.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.TimerState
import com.wordco.clockworkandroid.session_list_feature.domain.use_case.GetAllTodoSessionsUseCase
import com.wordco.clockworkandroid.session_list_feature.domain.use_case.GetNewSessionComparatorUseCase
import com.wordco.clockworkandroid.session_list_feature.domain.use_case.GetSuspendedSessionComparatorUseCase
import com.wordco.clockworkandroid.session_list_feature.domain.use_case.GetTimerStateUseCase
import com.wordco.clockworkandroid.session_list_feature.ui.model.ActiveTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.TodoListData
import com.wordco.clockworkandroid.session_list_feature.ui.model.mapper.toNewTaskListItem
import com.wordco.clockworkandroid.session_list_feature.ui.model.mapper.toSuspendedTaskListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TaskListViewModel(
    private val getTimerStateUseCase: GetTimerStateUseCase,
    private val getAllTodoSessionsUseCase: GetAllTodoSessionsUseCase,
    private val getNewSessionComparatorUseCase: GetNewSessionComparatorUseCase,
    private val getSuspendedSessionComparatorUseCase: GetSuspendedSessionComparatorUseCase,
) : ViewModel() {

    private val currentBehavior = MutableStateFlow<PageBehavior>(LoadingBehavior(
        initialUiState = TaskListUiState.Retrieving
    ))

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = currentBehavior
        .flatMapLatest { behavior -> behavior.uiState }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = currentBehavior.value.uiState.value
        )

    private val _uiEffect = Channel<TodoListUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEvent(event: TodoListUiEvent) {
        viewModelScope.launch {
            currentBehavior.value.handle(event)
        }
    }

    suspend fun sendEffect(effect: TodoListUiEffect) {
        _uiEffect.send(effect)
    }

    private interface PageBehavior {
        val uiState: StateFlow<TaskListUiState>
        suspend fun handle(event: TodoListUiEvent)
    }

    private inner class LoadingBehavior(
        initialUiState: TaskListUiState.Retrieving,
    ) : PageBehavior {
        override val uiState = MutableStateFlow(initialUiState)

        init {
            viewModelScope.launch {
                try {
                    val dataFlow = combine(
                        getAllTodoSessionsUseCase(),
                        getTimerStateUseCase(),
                        getNewSessionComparatorUseCase(),
                        getSuspendedSessionComparatorUseCase()
                    ) { sessions, timerState, newComparator, suspendedComparator ->

                        val activeSession = (timerState as? TimerState.Active)?.let { activeTimer ->
                            sessions.firstOrNull { it.taskId == activeTimer.taskId }?.let { session ->
                                ActiveTaskListItem.from(session, activeTimer)
                            } ?: throw RuntimeException("Active session does not exist")
                        }

                        val newSessions = sessions
                            .filterIsInstance<NewTask>()
                            .sortedWith(newComparator)
                            .map { it.toNewTaskListItem() }

                        val suspendedSessions = sessions
                            .filterIsInstance<StartedTask>()
                            .filter { it.taskId != activeSession?.taskId }
                            .sortedWith(suspendedComparator)
                            .map { it.toSuspendedTaskListItem() }

                        TodoListData(
                            activeSession = activeSession,
                            newSessions = newSessions,
                            suspendedSessions = suspendedSessions
                        )
                    }.shareIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(5000),
                        replay = 1
                    )

                    val initialData = dataFlow.first()

                    val initialUiState = if (initialData.activeSession != null) {
                        TaskListUiState.TimerActive(
                            newTasks = initialData.newSessions,
                            suspendedTasks = initialData.suspendedSessions,
                            activeTask = initialData.activeSession
                        )
                    } else {
                        TaskListUiState.TimerDormant(
                            newTasks = initialData.newSessions,
                            suspendedTasks = initialData.suspendedSessions,
                        )
                    }

                    currentBehavior.update {
                        ListBehavior(
                            initialUiState = initialUiState,
                            todoListData = dataFlow,
                        )
                    }
                } catch (e: Exception) {
                    currentBehavior.update {
                        ErrorBehavior(
                            initialUiState = TaskListUiState.Error(
                                header = "Initialization Failed",
                                message = e.message ?: "No Message",
                            ),
                            stackTrace = e.stackTraceToString(),
                        )
                    }
                }
            }
        }

        override suspend fun handle(event: TodoListUiEvent) {
            when (event as? TodoListUiEvent.LoadingEvent) {
                null -> {}
            }
        }
    }

    private inner class ErrorBehavior(
        initialUiState: TaskListUiState.Error,
        val stackTrace: String?,
    ) : PageBehavior {
        override val uiState = MutableStateFlow(initialUiState)

        override suspend fun handle(event: TodoListUiEvent) {
            when(event as? TodoListUiEvent.ErrorEvent) {
                TodoListUiEvent.CopyErrorClicked -> copyError()
                null -> { }
            }
        }

        suspend fun copyError() {
            val clipboardContent = """
                    Title: ${uiState.value.header}
                    Message: ${uiState.value.message}
                    --- StackTrace ---
                    $stackTrace
                """.trimIndent()

            sendEffect(TodoListUiEffect.CopyToClipboard(clipboardContent))
            sendEffect(TodoListUiEffect.ShowSnackbar("Error info copied"))
        }
    }

    private inner class ListBehavior(
        initialUiState: TaskListUiState.Retrieved,
        todoListData: Flow<TodoListData>,
    ) : PageBehavior {
        override val uiState = todoListData.map { data ->
            if (data.activeSession != null) {
                TaskListUiState.TimerActive(
                    newTasks = data.newSessions,
                    suspendedTasks = data.suspendedSessions,
                    activeTask = data.activeSession
                )
            } else {
                TaskListUiState.TimerDormant(
                    newTasks = data.newSessions,
                    suspendedTasks = data.suspendedSessions,
                )
            }
        }.catch { e ->
            currentBehavior.update {
                ErrorBehavior(
                    initialUiState = TaskListUiState.Error(
                        header = "Loading Failed",
                        message = e.message ?: "Encountered Loading Error"
                    ),
                    stackTrace = e.stackTraceToString()
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = initialUiState
        )

        override suspend fun handle(event: TodoListUiEvent) {
            when (val e = event as? TodoListUiEvent.ListEvent) {
                null -> { }
                TodoListUiEvent.CreateSessionClicked -> sendEffect(TodoListUiEffect.NavigateToCreateSession)
                is TodoListUiEvent.SessionClicked -> sendEffect(TodoListUiEffect.NavigateToSessionDetails(e.id))
            }
        }

    }


    companion object {


        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer

                TaskListViewModel(
                    getTimerStateUseCase = appContainer.getTimerStateUseCase,
                    getAllTodoSessionsUseCase = appContainer.getAllTodoSessionsUseCase,
                    getNewSessionComparatorUseCase = appContainer.getNewSessionComparatorUseCase,
                    getSuspendedSessionComparatorUseCase = appContainer.getSuspendedSessionComparatorUseCase
                )
            }
        }
    }
}