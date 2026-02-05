package com.wordco.clockworkandroid.profile_details_feature.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.use_case.GetProfileUseCase
import com.wordco.clockworkandroid.profile_details_feature.domain.use_case.DeleteProfileUseCase
import com.wordco.clockworkandroid.profile_details_feature.ui.model.ProfileDetailsModal
import com.wordco.clockworkandroid.profile_details_feature.ui.model.ViewModelManagedUiState
import com.wordco.clockworkandroid.profile_details_feature.ui.model.mapper.toCompletedSessionListItem
import com.wordco.clockworkandroid.profile_details_feature.ui.model.mapper.toTodoSessionListItem
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileDetailsViewModel(
    private val profileId: Long,
    private val getProfileUseCase: GetProfileUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase,
) : ViewModel() {

    private val currentBehavior = MutableStateFlow<PageBehavior>(LoadingBehavior(
        initialUiState = ProfileDetailsUiState.Retrieving
    ))

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = currentBehavior
        .flatMapLatest { it.uiState }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = currentBehavior.value.uiState.value
        )

    private val _uiEffect = Channel<ProfileDetailsUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEvent(event: ProfileDetailsUiEvent) {
        viewModelScope.launch {
            currentBehavior.value.handle(event)
        }
    }

    private suspend fun sendEffect(effect: ProfileDetailsUiEffect){
        _uiEffect.send(effect)
    }

    private interface PageBehavior {
        val uiState: StateFlow<ProfileDetailsUiState>
        suspend fun handle(event: ProfileDetailsUiEvent)
    }

    private inner class LoadingBehavior(
        initialUiState: ProfileDetailsUiState.Retrieving
    ) : PageBehavior {
        override val uiState = MutableStateFlow(initialUiState)

        init {
            viewModelScope.launch {
                try {
                    val sharedProfileFlow = getProfileUseCase(profileId)
                        .shareIn(
                            scope = viewModelScope,
                            started = SharingStarted.Lazily,
                            replay = 1, // cache latest session
                        )

                    val profile = sharedProfileFlow.first() // get from cache

                    val (completeSessions, todoSessions) = profile.sessions
                        .partition { it is CompletedTask }

                    val initialDetailsState = ProfileDetailsUiState.Retrieved(
                        profileName = profile.name,
                        profileColor = profile.color,
                        todoSessions = todoSessions.map {
                            it.toTodoSessionListItem()
                        },
                        completeSessions = completeSessions.map {
                            (it as CompletedTask).toCompletedSessionListItem()
                        },
                        isMenuOpen = false,
                        currentModal = null,
                    )

                    currentBehavior.update {
                        DetailsBehavior(
                            initialUiState = initialDetailsState,
                            profile = sharedProfileFlow,
                        )
                    }

                } catch (e: Exception) {
                    currentBehavior.update {
                        ErrorBehavior(
                            initialUiState = ProfileDetailsUiState.Error(
                                header = "Initialization Failed",
                                message = e.message ?: "No Message",
                            ),
                            stackTrace = e.stackTraceToString()
                        )
                    }
                }
            }
        }

        override suspend fun handle(event: ProfileDetailsUiEvent) {
            when (event as? ProfileDetailsUiEvent.LoadingEvent) {
                ProfileDetailsUiEvent.BackClicked -> sendEffect(ProfileDetailsUiEffect.NavigateBack)
                null -> {}
            }
        }
    }

    private inner class ErrorBehavior(
        initialUiState: ProfileDetailsUiState.Error,
        val stackTrace: String?,
    ) : PageBehavior {
        override val uiState = MutableStateFlow(initialUiState)

        override suspend fun handle(event: ProfileDetailsUiEvent) {
            when (event as? ProfileDetailsUiEvent.ErrorEvent) {
                ProfileDetailsUiEvent.BackClicked -> sendEffect(ProfileDetailsUiEffect.NavigateBack)
                ProfileDetailsUiEvent.CopyErrorClicked -> copyError()
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

            sendEffect(ProfileDetailsUiEffect.CopyToClipboard(clipboardContent))
            sendEffect(ProfileDetailsUiEffect.ShowSnackbar("Error info copied"))
        }
    }

    private inner class DetailsBehavior(
        initialUiState: ProfileDetailsUiState.Retrieved,
        profile: Flow<Profile>,
    ) : PageBehavior {
        private val localState = MutableStateFlow(ViewModelManagedUiState(
                isMenuOpen = initialUiState.isMenuOpen,
                currentModal = initialUiState.currentModal,
            )
        )

        override val uiState = combine(
            localState,
            profile,
        ) { localState, profile ->
            val (completeSessions, todoSessions) = profile.sessions
                .partition { it is CompletedTask }

            ProfileDetailsUiState.Retrieved(
                profileName = profile.name,
                profileColor = profile.color,
                todoSessions = todoSessions.map {
                    it.toTodoSessionListItem()
                },
                completeSessions = completeSessions.map {
                    (it as CompletedTask).toCompletedSessionListItem()
                },
                isMenuOpen = localState.isMenuOpen,
                currentModal = localState.currentModal,
            )
        }.catch { e ->
            currentBehavior.update {
                ErrorBehavior(
                    initialUiState = ProfileDetailsUiState.Error(
                        header = "Session Lost",
                        message = e.message ?: "Stream Interrupted"
                    ),
                    stackTrace = e.stackTraceToString()
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = initialUiState
        )

        override suspend fun handle(event: ProfileDetailsUiEvent) {
            when (val e = event as? ProfileDetailsUiEvent.DetailsEvent) {
                ProfileDetailsUiEvent.BackClicked -> sendEffect(ProfileDetailsUiEffect.NavigateBack)
                is ProfileDetailsUiEvent.CompletedSessionClicked -> sendEffect(ProfileDetailsUiEffect.NavigateToCompletedSession(e.id))
                ProfileDetailsUiEvent.CreateSessionClicked -> sendEffect(ProfileDetailsUiEffect.NavigateToCreateSession)
                ProfileDetailsUiEvent.DeleteClicked -> showDeleteSessionConfirmationModal()
                ProfileDetailsUiEvent.DeleteConfirmed -> triggerDeleteSession()
                ProfileDetailsUiEvent.EditClicked -> sendEffect(ProfileDetailsUiEffect.NavigateToProfileEditor)
                ProfileDetailsUiEvent.ModalDismissed -> dismissModals()
                is ProfileDetailsUiEvent.TodoSessionClicked -> sendEffect(ProfileDetailsUiEffect.NavigateToTodoSession(e.id))
                ProfileDetailsUiEvent.MenuOpened -> openMenu()
                ProfileDetailsUiEvent.MenuClosed -> closeMenu()
                null -> {}
            }
        }

        fun showDeleteSessionConfirmationModal() {
            localState.update { it.copy(currentModal = ProfileDetailsModal.DeleteConfirmation) }
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
                initialUiState = ProfileDetailsUiState.Deleting
            ) }
        }
    }

    private inner class DeletingBehavior(
        initialUiState: ProfileDetailsUiState.Deleting
    ) : PageBehavior {
        override val uiState = MutableStateFlow(initialUiState)

        init {
            viewModelScope.launch {
                try {
                    deleteProfileUseCase(profileId)
                    sendEffect(ProfileDetailsUiEffect.NavigateBack)

                } catch (e: Exception) {
                    currentBehavior.update {
                        ErrorBehavior(
                            initialUiState = ProfileDetailsUiState.Error(
                                header = "Deletion Failed",
                                message = e.message ?: "Could not delete task"
                            ),
                            stackTrace = e.stackTraceToString()
                        )
                    }
                }
            }
        }

        override suspend fun handle(event: ProfileDetailsUiEvent) {
            when (event as? ProfileDetailsUiEvent.DeletingEvent) {
                null -> { }
            }
        }

    }



    companion object {

        val PROFILE_ID_KEY = object : CreationExtras.Key<Long> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val profileId = this[PROFILE_ID_KEY] as Long
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer

                ProfileDetailsViewModel(
                    profileId = profileId,
                    getProfileUseCase = appContainer.getProfileUseCase,
                    deleteProfileUseCase = appContainer.deleteProfileUseCase,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}