package com.wordco.clockworkandroid.edit_session_feature.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.use_case.GetAllProfilesUseCase
import com.wordco.clockworkandroid.edit_session_feature.ui.model.ProfilePickerItem
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toProfilePickerItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfilePickerViewModel(
    private val initialSelection: Long?,
    private val getAllProfilesUseCase: GetAllProfilesUseCase,
) : ViewModel() {

    private val _currentBehavior = MutableStateFlow<PageBehavior>(
        LoadingBehavior()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = _currentBehavior
        .flatMapLatest { behavior -> behavior.uiState }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _currentBehavior.value.uiState.value
        )

    private val _uiEffect = Channel<ProfilePickerUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private interface PageBehavior {
        val uiState: StateFlow<ProfilePickerUiState>
        suspend fun handle(event: ProfilePickerUiEvent)
    }

    private inner class LoadingBehavior(
        initialUiState: ProfilePickerUiState.Retrieving = ProfilePickerUiState.Retrieving
    ): PageBehavior {
        override val uiState = MutableStateFlow( initialUiState)

        override suspend fun handle(event: ProfilePickerUiEvent) {
            when (event as? ProfilePickerUiEvent.LoadingEvent) {
                ProfilePickerUiEvent.BackClicked -> sendEffect(ProfilePickerUiEffect.NavigateBack)

                null -> { }
            }
        }
    }

    private inner class ErrorBehavior(
        initialUiState: ProfilePickerUiState.Error,
        private val stackTrace: String
    ): PageBehavior {
        override val uiState = MutableStateFlow(initialUiState)

        override suspend fun handle(event: ProfilePickerUiEvent) {
            when (event as? ProfilePickerUiEvent.ErrorEvent) {
                ProfilePickerUiEvent.BackClicked -> sendEffect(ProfilePickerUiEffect.NavigateBack)

                ProfilePickerUiEvent.CopyErrorClicked -> copyError()

                null -> { }
            }
        }

        private suspend fun copyError() {
            val clipboardContent = """
                    Title: ${uiState.value.header}
                    Message: ${uiState.value.message}
                    --- StackTrace ---
                    $stackTrace
                """.trimIndent()

            sendEffect(ProfilePickerUiEffect.CopyToClipboard(clipboardContent))
            sendEffect(ProfilePickerUiEffect.ShowSnackbar("Error info copied"))
        }
    }

    private inner class PickerBehavior(
        initialUiState: ProfilePickerUiState.Retrieved,
    ): PageBehavior {
        override val uiState = MutableStateFlow(initialUiState)

        fun updateProfiles(newProfiles: List<ProfilePickerItem>) {
            uiState.update { it.copy(profiles = newProfiles) }
        }

        override suspend fun handle(event: ProfilePickerUiEvent) {
            when (val e = event as? ProfilePickerUiEvent.PickerEvent) {
                is ProfilePickerUiEvent.ProfileClicked -> {
                    sendEffect(ProfilePickerUiEffect.FinishWithResult(e.id))
                }

                ProfilePickerUiEvent.BackClicked -> {
                    sendEffect(ProfilePickerUiEffect.NavigateBack)
                }

                ProfilePickerUiEvent.CreateProfileClicked -> {
                    sendEffect(ProfilePickerUiEffect.NavigateToCreateProfile)
                }

                null -> { }
            }
        }
    }


    init {
        loadProfiles()
    }

    private fun loadProfiles() {
        getAllProfilesUseCase()
            .onEach { profiles ->
                val pickerItems = profiles.map { it.toProfilePickerItem() }

                // If we are already Retrieved, just update the list (preserves scroll/focus if any).
                // If we are Retrieving or Error, switch to Retrieved.
                val current = _currentBehavior.value
                if (current is PickerBehavior) {
                    current.updateProfiles(pickerItems)
                } else {
                    _currentBehavior.update {
                        PickerBehavior(ProfilePickerUiState.Retrieved(
                            profiles = pickerItems,
                            selectedProfileId = initialSelection,
                        ))
                    }
                }
            }
            .catch { e ->
                _currentBehavior.update {
                    ErrorBehavior(
                        ProfilePickerUiState.Error(
                            header = "Failed to Load Templates",
                            message = e.message ?: "No message"
                        ),
                        stackTrace = e.stackTraceToString()
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: ProfilePickerUiEvent) {
        viewModelScope.launch { _currentBehavior.value.handle(event) }
    }

    private suspend fun sendEffect(effect: ProfilePickerUiEffect) {
        _uiEffect.send(effect)
    }

    companion object {

        val SELECTED_PROFILE_KEY = object : CreationExtras.Key<Long?> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer
                val selectedProfile = this[SELECTED_PROFILE_KEY]

                ProfilePickerViewModel(
                    getAllProfilesUseCase = appContainer.getAllProfilesUseCase,
                    initialSelection = selectedProfile,
                )
            }
        }
    }
}