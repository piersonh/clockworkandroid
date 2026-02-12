package com.wordco.clockworkandroid.profile_list_feature.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.use_case.GetAllProfilesUseCase
import com.wordco.clockworkandroid.profile_list_feature.ui.model.ProfileListItem
import com.wordco.clockworkandroid.profile_list_feature.ui.model.mapper.toProfileListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileListViewModel(
    private val getAllProfilesUseCase: GetAllProfilesUseCase,
) : ViewModel() {

    private val currentBehavior = MutableStateFlow<PageBehavior>(LoadingBehavior(
        initialUiState = ProfileListUiState.Retrieving
    ))

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = currentBehavior
        .flatMapLatest { behavior -> behavior.uiState }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = currentBehavior.value.uiState.value
        )

    private val _uiEffect = Channel<ProfileListUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEvent(event: ProfileListUiEvent) {
        viewModelScope.launch {
            currentBehavior.value.handle(event)
        }
    }

    suspend fun sendEffect(effect: ProfileListUiEffect) {
        _uiEffect.send(effect)
    }

    private interface PageBehavior {
        val uiState: StateFlow<ProfileListUiState>
        suspend fun handle(event: ProfileListUiEvent)
    }

    private inner class LoadingBehavior(
        initialUiState: ProfileListUiState.Retrieving,
    ) : PageBehavior {
        override val uiState = MutableStateFlow(initialUiState)

        init {
            viewModelScope.launch {
                try {
                    val profileListFlow = getAllProfilesUseCase()
                        .map { list -> list.map { it.toProfileListItem() } }
                        .shareIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            replay = 1, // cache latest session
                        )

                    val profiles = profileListFlow.first()

                    val initialListUiState = ProfileListUiState.Retrieved(
                        profiles = profiles
                    )

                    currentBehavior.update {
                        ListBehavior(
                            initialUiState = initialListUiState,
                            profiles = profileListFlow
                        )
                    }
                } catch (e: Exception) {
                    currentBehavior.update {
                        ErrorBehavior(
                            initialUiState = ProfileListUiState.Error(
                                header = "Initialization Failed",
                                message = e.message ?: "No Message",
                            ),
                            stackTrace = e.stackTraceToString(),
                        )
                    }
                }
            }
        }

        override suspend fun handle(event: ProfileListUiEvent) {
            when (event as? ProfileListUiEvent.LoadingEvent) {
                null -> {}
            }
        }
    }

    private inner class ErrorBehavior(
        initialUiState: ProfileListUiState.Error,
        val stackTrace: String?,
    ) : PageBehavior {
        override val uiState = MutableStateFlow(initialUiState)

        override suspend fun handle(event: ProfileListUiEvent) {
            when(event as? ProfileListUiEvent.ErrorEvent) {
                ProfileListUiEvent.CopyErrorClicked -> copyError()
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

            sendEffect(ProfileListUiEffect.CopyToClipboard(clipboardContent))
            sendEffect(ProfileListUiEffect.ShowSnackbar("Error info copied"))
        }
    }

    private inner class ListBehavior(
        initialUiState: ProfileListUiState.Retrieved,
        profiles: Flow<List<ProfileListItem>>
    ) : PageBehavior {
        override val uiState = profiles.map { listItems ->
            ProfileListUiState.Retrieved(listItems)
        }.catch { e ->
            currentBehavior.update {
                ErrorBehavior(
                    initialUiState = ProfileListUiState.Error(
                        header = "Profiles Lost",
                        message = e.message ?: "Error collecting profiles"
                    ),
                    stackTrace = e.stackTraceToString()
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = initialUiState
        )

        override suspend fun handle(event: ProfileListUiEvent) {
            when (val e = event as? ProfileListUiEvent.ListEvent) {
                null -> { }
                ProfileListUiEvent.CreateProfileClicked -> sendEffect(ProfileListUiEffect.NavigateToCreateProfile)
                is ProfileListUiEvent.ProfileClicked -> sendEffect(ProfileListUiEffect.NavigateToProfile(e.id))
            }
        }

    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer
                val getAllProfilesUseCase = appContainer.getAllProfilesUseCase
                //val timer = (this[APPLICATION_KEY] as MainApplication).timer


                ProfileListViewModel (
                    getAllProfilesUseCase = getAllProfilesUseCase
                    //timer = timer,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}