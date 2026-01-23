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
import com.wordco.clockworkandroid.session_completion_feature.ui.model.SessionReportModal
import com.wordco.clockworkandroid.session_completion_feature.ui.model.ViewModelManagedUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TaskCompletionViewModel (
    private val taskId: Long,
    private val getSessionUseCase: GetSessionUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val calculateEstimateAccuracyUseCase: CalculateEstimateAccuracyUseCase,
) : ViewModel() {
    private interface PageBehavior {
        val uiState: StateFlow<TaskCompletionUiState>
        suspend fun handle(event: SessionReportUiEvent)
    }

    private inner class LoadingBehavior(
        initialUiState: TaskCompletionUiState.Retrieving
    ) : PageBehavior {
        override val uiState = MutableStateFlow(initialUiState)

        init {
            viewModelScope.launch {
                try {
                    val sharedSessionFlow = getSessionUseCase(taskId)
                        .onEach {
                            if (it !is CompletedTask) throw IllegalStateException("Session not completed")
                        }
                        .filterIsInstance<CompletedTask>()
                        .shareIn(
                            scope = viewModelScope,
                            started = SharingStarted.Lazily, // wait for validation check below
                            replay = 1, // cache latest session that was just validated
                        )

                    // triggers the upstream logic
                    val session = sharedSessionFlow.first()

                    val initialReportState = TaskCompletionUiState.Retrieved.from(
                        session = session,
                        viewModelManagedUiState = ViewModelManagedUiState(
                            isMenuOpen = false,
                            currentModal = null
                        ),
                        accuracyCalculator = calculateEstimateAccuracyUseCase::invoke
                    )

                    currentBehavior.update {
                        ReportBehavior(
                            initialUiState = initialReportState,
                            session = sharedSessionFlow
                        )
                    }

                } catch (e: Exception) {
                    currentBehavior.update {
                        ErrorBehavior(
                            initialUiState = TaskCompletionUiState.Error(
                                header = "Initialization Failed",
                                message = e.message ?: "No Message"
                            ),
                            stackTrace = e.stackTraceToString()
                        )
                    }
                }
            }
        }

        override suspend fun handle(event: SessionReportUiEvent) {
            when (event as? SessionReportUiEvent.LoadingEvent) {
                SessionReportUiEvent.BackClicked -> sendEffect(TaskCompletionUiEffect.NavigateBack)
                null -> {}
            }
        }
    }

    private inner class ErrorBehavior(
        initialUiState: TaskCompletionUiState.Error,
        val stackTrace: String?,
    ) : PageBehavior {
        override val uiState = MutableStateFlow(initialUiState)

        override suspend fun handle(event: SessionReportUiEvent) {
            when (event as? SessionReportUiEvent.ErrorEvent) {
                SessionReportUiEvent.BackClicked -> sendEffect(TaskCompletionUiEffect.NavigateBack)
                SessionReportUiEvent.CopyErrorClicked -> copyError()
                null -> {}
            }
        }

        suspend fun copyError() {
            val clipboardContent = """
                    Title: ${uiState.value.header}
                    Message: ${uiState.value.message}
                    --- StackTrace ---
                    $stackTrace
                """.trimIndent()

            sendEffect(TaskCompletionUiEffect.CopyToClipboard(clipboardContent))
            sendEffect(TaskCompletionUiEffect.ShowSnackbar("Error info copied"))
        }
    }

    private inner class ReportBehavior(
        initialUiState: TaskCompletionUiState.Retrieved,
        session: Flow<CompletedTask>,
    ) : PageBehavior {
        private val localState = MutableStateFlow(ViewModelManagedUiState(
            isMenuOpen = initialUiState.isMenuOpen,
            currentModal = initialUiState.currentModal,
        ))

        override val uiState = combine(
            session,
            localState
        ){ session, localState ->
            TaskCompletionUiState.Retrieved.from(
                session = session,
                viewModelManagedUiState = localState,
                accuracyCalculator = calculateEstimateAccuracyUseCase::invoke
            )
        }.catch { e ->
                currentBehavior.update {
                    ErrorBehavior(
                        initialUiState = TaskCompletionUiState.Error(
                            header = "Session Lost",
                            message = e.message ?: "Stream Interrupted"
                        ),
                        stackTrace = e.stackTraceToString()
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = initialUiState
            )

        override suspend fun handle(event: SessionReportUiEvent) {
            when (event as? SessionReportUiEvent.ReportEvent) {
                SessionReportUiEvent.BackClicked -> sendEffect(TaskCompletionUiEffect.NavigateBack)
                SessionReportUiEvent.DeleteClicked -> showDeleteSessionConfirmationModal()
                SessionReportUiEvent.ContinueClicked -> sendEffect(TaskCompletionUiEffect.NavigateToContinue)
                SessionReportUiEvent.EditClicked -> sendEffect(TaskCompletionUiEffect.NavigateToEditSession)
                SessionReportUiEvent.DeleteConfirmed -> triggerDeleteSession()
                SessionReportUiEvent.MenuClosed -> closeMenu()
                SessionReportUiEvent.MenuOpened -> openMenu()
                SessionReportUiEvent.ModalDismissed -> dismissModals()
                null -> {}
            }
        }

        fun showDeleteSessionConfirmationModal() {
            localState.update { it.copy(currentModal = SessionReportModal.DeleteConfirmation) }
        }

        fun dismissModals() {
            localState.update { it.copy(currentModal = null) }
        }

        fun openMenu() {
            localState.update { it.copy(isMenuOpen = true) }
        }

        fun closeMenu() {
            localState.update { it.copy(isMenuOpen = false) }
        }

        fun triggerDeleteSession() {
            currentBehavior.update { DeletingBehavior(
                initialUiState = TaskCompletionUiState.Deleting
            ) }
        }
    }

    private inner class DeletingBehavior(
        initialUiState: TaskCompletionUiState.Deleting,
    ) : PageBehavior {
        override val uiState = MutableStateFlow(initialUiState)

        init {
            viewModelScope.launch {
                try {
                    deleteSessionUseCase(taskId)
                    sendEffect(TaskCompletionUiEffect.NavigateBack)

                } catch (e: Exception) {
                    currentBehavior.update {
                        ErrorBehavior(
                            initialUiState = TaskCompletionUiState.Error(
                                header = "Deletion Failed",
                                message = e.message ?: "Could not delete task"
                            ),
                            stackTrace = e.stackTraceToString()
                        )
                    }
                }
            }
        }

        override suspend fun handle(event: SessionReportUiEvent) {
            when (event as? SessionReportUiEvent.DeletingEvent) {
                null -> {}
            }
        }

    }

    private val currentBehavior = MutableStateFlow<PageBehavior>(LoadingBehavior(
        initialUiState = TaskCompletionUiState.Retrieving
    ))

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = currentBehavior.flatMapLatest { behavior -> behavior.uiState }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            currentBehavior.value.uiState.value
        )

    private val _uiEffect = Channel<TaskCompletionUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()


    fun onEvent(event: SessionReportUiEvent) {
        viewModelScope.launch {
            currentBehavior.value.handle(event)
        }
    }

    suspend fun sendEffect(effect: TaskCompletionUiEffect) {
        _uiEffect.send(effect)
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