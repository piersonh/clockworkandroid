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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    private sealed interface InternalState {
        val uiState: ProfileFormUiState
        val behavior: PageBehavior

        data class Loading(
            override val uiState: ProfileFormUiState.Retrieving,
            override val behavior: LoadingBehavior
        ): InternalState

        data class Failed(
            override val uiState: ProfileFormUiState.Error,
            override val behavior: ErrorBehavior
        ): InternalState

        sealed interface Active : InternalState {
            override val uiState: ProfileFormUiState.Retrieved
            fun updateUi(func: ProfileFormUiState.Retrieved.() -> ProfileFormUiState.Retrieved): Active
        }

        data class Create(
            override val uiState: ProfileFormUiState.Retrieved,
            override val behavior: CreateFormBehavior
        ): Active {
            override fun updateUi(
                func: ProfileFormUiState.Retrieved.() -> ProfileFormUiState.Retrieved
            ): Active {
                return copy(uiState = uiState.func())
            }
        }

        data class Edit(
            override val uiState: ProfileFormUiState.Retrieved,
            override val behavior: EditFormBehavior
        ): Active {
            override fun updateUi(
                func: ProfileFormUiState.Retrieved.() -> ProfileFormUiState.Retrieved
            ): Active {
                return copy(uiState = uiState.func())
            }
        }
    }

    private val _internalState = MutableStateFlow<InternalState>(
        InternalState.Loading(
            uiState = ProfileFormUiState.Retrieving(
                when(formMode) {
                    ProfileFormMode.Create -> "Create Profile"
                    is ProfileFormMode.Edit -> "Edit Profile"
                }
            ),
            behavior = LoadingBehavior()
        )
    )

    val uiState = _internalState
        .map { it.uiState }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            _internalState.value.uiState
        )

    private val _uiEffect = Channel<ProfileFormUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private interface PageBehavior {
        fun handle(event: ProfileFormUiEvent)
    }

    private inner class LoadingBehavior: PageBehavior {
        override fun handle(event: ProfileFormUiEvent) {
            when(event as? ProfileFormUiEvent.LoadingEvent) {
                ProfileFormUiEvent.BackClicked -> sendEffect(ProfileFormUiEffect.NavigateBack)
                null -> { }
            }
        }
    }

    private inner class ErrorBehavior(
        val alert: String,
        val message: String,
        val stackTrace: String?,
    ): PageBehavior {
        override fun handle(event: ProfileFormUiEvent) {
            when(event as? ProfileFormUiEvent.ErrorEvent) {
                ProfileFormUiEvent.CopyErrorClicked -> copyError()
                ProfileFormUiEvent.BackClicked -> sendEffect(ProfileFormUiEffect.NavigateBack)
                null -> { }
            }
        }

        fun copyError() {
            val clipboardContent = """
                    Title: $alert
                    Message: $message
                    --- StackTrace ---
                    $stackTrace
                """.trimIndent()

            sendEffect(ProfileFormUiEffect.CopyToClipboard(clipboardContent))
            sendEffect(ProfileFormUiEffect.ShowSnackbar("Error info copied"))
        }
    }

    private abstract inner class FormBehavior : PageBehavior {
        abstract fun save()

        override fun handle(event: ProfileFormUiEvent) {
            when (val e = event as? ProfileFormUiEvent.FormEvent) {
                ProfileFormUiEvent.BackClicked -> handleBackClick()
                is ProfileFormUiEvent.NameChanged -> updateState { copy(name = e.newName, hasFormChanges = true) }
                is ProfileFormUiEvent.ColorSliderChanged -> updateState { copy(colorSliderPos = e.newValue, hasFormChanges = true) }
                is ProfileFormUiEvent.DifficultySliderChanged -> updateState { copy(difficulty = e.newValue, hasFormChanges = true) }
                ProfileFormUiEvent.DiscardConfirmed -> discardAndClose()
                ProfileFormUiEvent.ModalDismissed -> updateState { copy(currentModal = null) }
                ProfileFormUiEvent.SaveClicked -> save()
                null -> { }
            }
        }

        protected fun updateState(block: ProfileFormUiState.Retrieved.() -> ProfileFormUiState.Retrieved) {
            _internalState.update { currentState ->
                (currentState as? InternalState.Active)?.updateUi(block) ?: currentState
            }
        }

        private fun handleBackClick() {
            val current = _internalState.value
            if (current is InternalState.Active && current.uiState.hasFormChanges) {
                updateState { copy(currentModal = ProfileFormModal.Discard) }
            } else {
                sendEffect(ProfileFormUiEffect.NavigateBack)
            }
        }

        private fun discardAndClose() {
            updateState { copy(currentModal = null) }
            sendEffect(ProfileFormUiEffect.NavigateBack)
        }
    }

    private inner class CreateFormBehavior: FormBehavior() {
        override fun save() {
            val currentState = (_internalState.value as? InternalState.Active)?.uiState ?: return

            if (currentState.name.isBlank()) {
                sendEffect(
                    ProfileFormUiEffect.ShowSnackbar(
                        "Please give the template a name."
                    )
                )
                return
            }

            viewModelScope.launch {
                createProfileUseCase(
                    Profile(
                        id = 0,
                        name = currentState.name,
                        color = Color.fromSlider(currentState.colorSliderPos),
                        defaultDifficulty = currentState.difficulty.toInt(),
                        sessions = emptyList(),
                    )
                )
                //sendEffect(ProfileFormUiEffect.ShowSnackbar("Template Saved"))
                sendEffect(ProfileFormUiEffect.NavigateBack)
            }
        }
    }

    private inner class EditFormBehavior(
        val profile: Profile,
    ): FormBehavior() {
        override fun save() {
            val currentState = (_internalState.value as? InternalState.Active)?.uiState ?: return

            if (currentState.name.isBlank()) {
                sendEffect(
                    ProfileFormUiEffect.ShowSnackbar(
                        "Please give the template a name."
                    )
                )
                return
            }

            viewModelScope.launch {
                updateProfileUseCase(
                    Profile(
                        id = profile.id,
                        name = currentState.name,
                        color = Color.fromSlider(currentState.colorSliderPos),
                        defaultDifficulty = currentState.difficulty.toInt(),
                        sessions = profile.sessions,
                    )
                )
                //sendEffect(ProfileFormUiEffect.ShowSnackbar("Template Saved"))
                sendEffect(ProfileFormUiEffect.NavigateBack)
            }
        }
    }

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
        _internalState.update {
            InternalState.Create(
                uiState = ProfileFormUiState.Retrieved(
                    title = "Create Profile",
                    name = fieldDefaults.name,
                    colorSliderPos = fieldDefaults.colorSliderPos,
                    difficulty = fieldDefaults.difficulty,
                    hasFormChanges = false,
                    currentModal = null,
                ),
                behavior = CreateFormBehavior()
            )
        }
    }

    private suspend fun setupEditMode(profileId: Long) {
        val profile = getProfileUseCase(profileId).first()

        _internalState.update {
            InternalState.Edit(
                uiState = ProfileFormUiState.Retrieved(
                    title = "Edit Profile",
                    name = profile.name,
                    colorSliderPos = profile.color.hue() / 360,
                    difficulty = profile.defaultDifficulty.toFloat(),
                    hasFormChanges = false,
                    currentModal = null,
                ),
                behavior = EditFormBehavior(profile)
            )
        }
    }

    private fun setFailure(alert: String, message: String, stackTrace: String?) {
        _internalState.update { currentState ->
            val pageTitle = currentState.uiState.title
            InternalState.Failed(
                uiState = ProfileFormUiState.Error(pageTitle, alert, message),
                behavior = ErrorBehavior(alert, message, stackTrace)
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
        _internalState.value.behavior.handle(event)
    }

    private fun sendEffect(effect: ProfileFormUiEffect) {
        viewModelScope.launch { _uiEffect.send(effect) }
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