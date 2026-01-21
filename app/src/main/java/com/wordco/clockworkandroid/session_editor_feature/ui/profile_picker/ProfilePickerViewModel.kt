package com.wordco.clockworkandroid.session_editor_feature.ui.profile_picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordco.clockworkandroid.core.domain.use_case.GetAllProfilesUseCase
import com.wordco.clockworkandroid.edit_session_feature.ui.model.mapper.toProfilePickerItem
import com.wordco.clockworkandroid.session_editor_feature.coordinator.SessionEditorManager
import com.wordco.clockworkandroid.session_editor_feature.coordinator.SessionEditorState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfilePickerViewModel(
    private val editorManager: SessionEditorManager,
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

        fun updateUi(func: (ProfilePickerUiState.Retrieved) -> ProfilePickerUiState.Retrieved) {
            uiState.update(func)
        }

        override suspend fun handle(event: ProfilePickerUiEvent) {
            when (val e = event as? ProfilePickerUiEvent.PickerEvent) {
                is ProfilePickerUiEvent.ProfileClicked -> {
                    editorManager.updateProfile(e.id)
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
        startCollection()
    }

    private fun startCollection() {
        val pickerItemsFlow = getAllProfilesUseCase()
            .map { profiles -> profiles.map { it.toProfilePickerItem() } }
            .map { Result.success(it) }
            .catch { e -> emit(Result.failure(e)) }

        combine(
            editorManager.state,
            pickerItemsFlow,
        ) { editorState, pickerItemsResult ->
            _currentBehavior.update { current ->

                val pickerItems = pickerItemsResult.getOrElse { e ->
                    return@update ErrorBehavior(
                        ProfilePickerUiState.Error(
                            header = "Failed to Load Templates",
                            message = e.message ?: "No message"
                        ),
                        stackTrace = e.stackTraceToString()
                    )
                }

                when (editorState) {
                    is SessionEditorState.Error -> {
                        val e = editorState.error
                        ErrorBehavior(
                            ProfilePickerUiState.Error(
                                header = editorState.alert,
                                message = e.message ?: "No message"
                            ),
                            stackTrace = e.stackTraceToString()
                        )
                    }
                    SessionEditorState.Retrieving -> {
                        LoadingBehavior()
                    }
                    is SessionEditorState.Retrieved -> {
                        if (current is PickerBehavior) {
                            current.apply { updateUi { it.copy(
                                profiles = pickerItems,
                                selectedProfileId = editorState.draft.profileId
                            ) } }
                        } else {
                            PickerBehavior(ProfilePickerUiState.Retrieved(
                                profiles = pickerItems,
                                selectedProfileId = editorState.draft.profileId,
                            ))
                        }
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: ProfilePickerUiEvent) {
        viewModelScope.launch { _currentBehavior.value.handle(event) }
    }

    private suspend fun sendEffect(effect: ProfilePickerUiEffect) {
        _uiEffect.send(effect)
    }
}