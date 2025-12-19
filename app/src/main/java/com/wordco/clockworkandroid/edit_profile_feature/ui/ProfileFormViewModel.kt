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
import com.wordco.clockworkandroid.edit_profile_feature.ui.util.updateRetrieved
import com.wordco.clockworkandroid.edit_profile_feature.ui.util.withRetrieved
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class ProfileFormViewModel(
    formMode: ProfileFormMode,
    private val getProfileUseCase: GetProfileUseCase,
    private val createProfileUseCase: CreateProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileFormUiState>(ProfileFormUiState.Retrieving(
        when(formMode) {
            ProfileFormMode.Create -> "Create Profile"
            is ProfileFormMode.Edit -> "Edit Profile"
        }
    ))
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<ProfileFormUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private sealed interface InternalState {
        data object Create : InternalState
        data class Edit(val profile: Profile): InternalState
    }

    private lateinit var internalState: InternalState

    private var fieldDefaults = getFieldDefaults()

    init {
        when(formMode) {
            ProfileFormMode.Create -> {
                internalState = InternalState.Create
                _uiState.update {
                    ProfileFormUiState.Retrieved(
                        title = "Create Profile",
                        name = fieldDefaults.name,
                        colorSliderPos = fieldDefaults.colorSliderPos,
                        difficulty = fieldDefaults.difficulty,
                        hasFormChanges = false,
                        currentModal = null,
                    )
                }
            }
            
            is ProfileFormMode.Edit -> {
                viewModelScope.launch {
                    val profile = getProfileUseCase(formMode.profileId).first()
                    internalState = InternalState.Edit(profile)
                    
                    _uiState.update {
                        ProfileFormUiState.Retrieved(
                            title = "EditProfile",
                            name = profile.name,
                            colorSliderPos = profile.color.hue() / 360,
                            difficulty = profile.defaultDifficulty.toFloat(),
                            hasFormChanges = false,
                            currentModal = null,
                        )
                    }
                }
            }
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
        when (event) {
            is ProfileFormUiEvent.BackClicked -> handleBackClick()
            ProfileFormUiEvent.ModalDismissed -> closeModals()
            ProfileFormUiEvent.SaveClicked -> validateAndSave()
            ProfileFormUiEvent.DiscardConfirmed -> discardAndClose()

            is ProfileFormUiEvent.ColorSliderChanged -> updateColor(event.newValue)
            is ProfileFormUiEvent.DifficultySliderChanged -> updateDifficulty(event.newValue)
            is ProfileFormUiEvent.NameChanged -> updateName(event.newName)
        }
    }

    private fun sendEffect(effect: ProfileFormUiEffect) {
        viewModelScope.launch { _uiEffect.send(effect) }
    }

    private fun handleBackClick() {
        val currentState = _uiState.value

        if(currentState is ProfileFormUiState.Retrieved && currentState.hasFormChanges) {
            _uiState.update { currentState.copy(currentModal = ProfileFormModal.Discard) }
        } else {
            sendEffect(ProfileFormUiEffect.NavigateBack)
        }
    }

    private fun updateName(newName: String) {
        _uiState.updateRetrieved { copy(name = newName, hasFormChanges = true) }
    }

    private fun updateColor(newVal: Float) {
        _uiState.updateRetrieved { copy(colorSliderPos = newVal, hasFormChanges = true) }
    }

    private fun updateDifficulty(newVal: Float) {
        _uiState.updateRetrieved { copy(difficulty = newVal, hasFormChanges = true) }
    }

    private fun closeModals() {
        _uiState.updateRetrieved { copy(currentModal = null) }
    }

    private fun validateAndSave() {
        _uiState.withRetrieved {
            if (name.isBlank()) {
                sendEffect(ProfileFormUiEffect.ShowSnackbar(
                    "Please give the template a name."
                ))
                return@withRetrieved
            }

            viewModelScope.launch {
                when (val state = internalState) {
                    InternalState.Create -> {
                        createProfileUseCase(
                            Profile(
                                id = 0,
                                name = name,
                                color = Color.fromSlider(colorSliderPos),
                                defaultDifficulty = difficulty.toInt(),
                                sessions = emptyList(),
                            )
                        )
                    }
                    is InternalState.Edit -> {
                        updateProfileUseCase(
                            Profile(
                                id = state.profile.id,
                                name = name,
                                color = Color.fromSlider(colorSliderPos),
                                defaultDifficulty = difficulty.toInt(),
                                sessions = state.profile.sessions,
                            )
                        )
                    }
                }
                //sendEffect(ProfileFormUiEffect.ShowSnackbar("Template Saved"))
                sendEffect(ProfileFormUiEffect.NavigateBack)
            }
        }
    }

    private fun discardAndClose() {
        closeModals()
        sendEffect(ProfileFormUiEffect.NavigateBack)
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