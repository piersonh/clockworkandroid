package com.wordco.clockworkandroid.edit_profile_feature.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.use_case.GetProfileUseCase
import com.wordco.clockworkandroid.core.ui.util.fromSlider
import com.wordco.clockworkandroid.core.ui.util.hue
import com.wordco.clockworkandroid.edit_profile_feature.domain.use_case.CreateProfileUseCase
import com.wordco.clockworkandroid.edit_profile_feature.domain.use_case.UpdateProfileUseCase
import com.wordco.clockworkandroid.edit_profile_feature.ui.model.ProfileFormDefaults
import com.wordco.clockworkandroid.edit_profile_feature.ui.model.ProfileFormModal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class ProfileFormViewModel(
    formMode: ProfileFormMode,
    private val getProfileUseCase: GetProfileUseCase,
    private val createProfileUseCase: CreateProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel() {
    private interface PageBehavior {
        val uiState: StateFlow<ProfileFormUiState>
        suspend fun handle(event: ProfileFormUiEvent)
    }

    private inner class LoadingBehavior(
        initialState: ProfileFormUiState.Retrieving
    ): PageBehavior {
        private val _state = MutableStateFlow(initialState)
        override val uiState = _state.asStateFlow()

        override suspend fun handle(event: ProfileFormUiEvent) {
            when(event as? ProfileFormUiEvent.LoadingEvent) {
                ProfileFormUiEvent.BackClicked -> sendEffect(ProfileFormUiEffect.NavigateBack)
                null -> { }
            }
        }
    }

    private inner class ErrorBehavior(
        initialState: ProfileFormUiState.Error,
        val stackTrace: String?,
    ): PageBehavior {
        private val _state = MutableStateFlow(initialState)
        override val uiState = _state.asStateFlow()

        override suspend fun handle(event: ProfileFormUiEvent) {
            when(event as? ProfileFormUiEvent.ErrorEvent) {
                ProfileFormUiEvent.CopyErrorClicked -> copyError()
                ProfileFormUiEvent.BackClicked -> sendEffect(ProfileFormUiEffect.NavigateBack)
                null -> { }
            }
        }

        suspend fun copyError() {
            val clipboardContent = """
                    Title: ${_state.value.title}
                    Message: ${_state.value.message}
                    --- StackTrace ---
                    $stackTrace
                """.trimIndent()

            sendEffect(ProfileFormUiEffect.CopyToClipboard(clipboardContent))
            sendEffect(ProfileFormUiEffect.ShowSnackbar("Error info copied"))
        }
    }

    private abstract inner class FormBehavior(
        initialState: ProfileFormUiState.Retrieved
    ) : PageBehavior {
        private val _formState = MutableStateFlow(initialState)
        override val uiState: StateFlow<ProfileFormUiState> = _formState.asStateFlow()

        abstract suspend fun save(state: ProfileFormUiState.Retrieved)

        override suspend fun handle(event: ProfileFormUiEvent) {
            when (val e = event as? ProfileFormUiEvent.FormEvent) {
                ProfileFormUiEvent.BackClicked -> handleBackClick()
                is ProfileFormUiEvent.NameChanged -> _formState.update { it.copy(name = e.newName, hasFormChanges = true) }
                is ProfileFormUiEvent.ColorSliderChanged -> _formState.update { it.copy(colorSliderPos = e.newValue, hasFormChanges = true) }
                is ProfileFormUiEvent.DifficultySliderChanged -> _formState.update { it.copy(difficulty = e.newValue, hasFormChanges = true) }
                ProfileFormUiEvent.DiscardConfirmed -> discardAndClose()
                ProfileFormUiEvent.ModalDismissed -> _formState.update { it.copy(currentModal = null) }
                ProfileFormUiEvent.SaveClicked -> validateAndSave()
                null -> { }
            }
        }

        private suspend fun validateAndSave() {
            val state = _formState.value
            if (state.name.isBlank()) {
                _uiEffect.send(ProfileFormUiEffect.ShowSnackbar("Please give the template a name."))
                return
            }
            save(state)
        }

        private suspend fun handleBackClick() {
            if (_formState.value.hasFormChanges) {
                _formState.update { it.copy(currentModal = ProfileFormModal.Discard) }
            } else {
                sendEffect(ProfileFormUiEffect.NavigateBack)
            }
        }

        private suspend fun discardAndClose() {
            _formState.update { it.copy(currentModal = null) }
            sendEffect(ProfileFormUiEffect.NavigateBack)
        }
    }

    private inner class CreateFormBehavior(
        initialState: ProfileFormUiState.Retrieved
    ): FormBehavior(initialState) {
        override suspend fun save(state: ProfileFormUiState.Retrieved) {
            createProfileUseCase(
                Profile(
                    id = 0,
                    name = state.name,
                    color = Color.fromSlider(state.colorSliderPos),
                    defaultDifficulty = state.difficulty.toInt(),
                    sessions = emptyList(),
                )
            )
            //sendEffect(ProfileFormUiEffect.ShowSnackbar("Template Saved"))
            sendEffect(ProfileFormUiEffect.NavigateBack)
        }
    }

    private inner class EditFormBehavior(
        initialState: ProfileFormUiState.Retrieved,
        val profile: Profile,
    ): FormBehavior(initialState) {
        override suspend fun save(state: ProfileFormUiState.Retrieved) {
            updateProfileUseCase(
                Profile(
                    id = profile.id,
                    name = state.name,
                    color = Color.fromSlider(state.colorSliderPos),
                    defaultDifficulty = state.difficulty.toInt(),
                    sessions = profile.sessions,
                )
            )
            //sendEffect(ProfileFormUiEffect.ShowSnackbar("Template Saved"))
            sendEffect(ProfileFormUiEffect.NavigateBack)
        }
    }


    private val _currentBehavior = MutableStateFlow<PageBehavior>(LoadingBehavior(
        initialState = ProfileFormUiState.Retrieving(
            title = when(formMode) {
                ProfileFormMode.Create -> "Create Template"
                is ProfileFormMode.Edit -> "Edit Template"
            }
        )
    ))


    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = _currentBehavior
        .flatMapLatest { behavior -> behavior.uiState }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            _currentBehavior.value.uiState.value
        )

    private val _uiEffect = Channel<ProfileFormUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private var fieldDefaults = getFieldDefaults()

    /**
     * Initialize the viewModel and page state
     */
    init {
        viewModelScope.launch {
            try {
                when(formMode) {
                    ProfileFormMode.Create -> setupCreateMode()
                    is ProfileFormMode.Edit -> setupEditMode(formMode.profileId)
                }
            } catch (e: Exception) {
                setFailure(
                    alert = "Failed to Load",
                    message = e.message ?: "No message",
                    stackTrace = e.stackTraceToString(),
                )
            }
        }
    }

    private fun setupCreateMode() {
        _currentBehavior.update {
            CreateFormBehavior(
                initialState = ProfileFormUiState.Retrieved(
                    title = "Create Template",
                    name = fieldDefaults.name,
                    colorSliderPos = fieldDefaults.colorSliderPos,
                    difficulty = fieldDefaults.difficulty,
                    hasFormChanges = false,
                    currentModal = null,
                ),
            )
        }
    }

    private suspend fun setupEditMode(profileId: Long) {
        val profile = getProfileUseCase(profileId).first()

        _currentBehavior.update {
            EditFormBehavior(
                initialState = ProfileFormUiState.Retrieved(
                    title = "Edit Template",
                    name = profile.name,
                    colorSliderPos = profile.color.hue() / 360,
                    difficulty = profile.defaultDifficulty.toFloat(),
                    hasFormChanges = false,
                    currentModal = null,
                ),
                profile = profile
            )
        }
    }

    private fun setFailure(alert: String, message: String, stackTrace: String?) {
        _currentBehavior.update { currentBehavior ->
            val pageTitle = currentBehavior.uiState.value.title

            ErrorBehavior(
                initialState = ProfileFormUiState.Error(pageTitle, alert, message),
                stackTrace = stackTrace
            )
        }
    }

    private fun getFieldDefaults(): ProfileFormDefaults {
        return ProfileFormDefaults(
            name = "",
            colorSliderPos = Random.nextFloat(),
            difficulty = 0f,
        )
    }

    fun onEvent(event: ProfileFormUiEvent) {
        viewModelScope.launch {
            _currentBehavior.value.handle(event)
        }
    }

    private suspend fun sendEffect(effect: ProfileFormUiEffect) {
        _uiEffect.send(effect)
    }


    companion object {
        val FORM_MODE_KEY = object : CreationExtras.Key<ProfileFormMode> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer

                val formMode = this[FORM_MODE_KEY] as ProfileFormMode

                ProfileFormViewModel(
                    formMode = formMode,
                    getProfileUseCase = appContainer.getProfileUseCase,
                    createProfileUseCase = appContainer.createProfileUseCase,
                    updateProfileUseCase = appContainer.updateProfileUseCase,
                )
            }
        }
    }
}