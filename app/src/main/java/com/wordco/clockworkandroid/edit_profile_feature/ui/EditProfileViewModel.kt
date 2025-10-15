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
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.core.ui.util.fromSlider
import com.wordco.clockworkandroid.core.ui.util.getIfType
import com.wordco.clockworkandroid.core.ui.util.hue
import com.wordco.clockworkandroid.edit_profile_feature.ui.util.updateIfRetrieved
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel (
    private val profileRepository: ProfileRepository,
    private val profileId: Long,
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditProfileUiState>(
        EditProfileUiState.Retrieving
    )

    val uiState = _uiState.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    private lateinit var _loadedProfile: Profile


    init {
        viewModelScope.launch {
            profileRepository.getProfile(profileId).first().run {
                _loadedProfile = this

                _uiState.update {
                    EditProfileUiState.Retrieved(
                        name = name,
                        colorSliderPos = color.hue() / 360,
                        difficulty = defaultDifficulty.toFloat(),
                        hasFieldChanges = false,
                    )
                }
            }
        }
    }


    fun onNameChange(newName: String) {
        _uiState.updateIfRetrieved { it.copy(
            name = newName,
            hasFieldChanges = true,
        ) }
    }

    fun onColorSliderChange(newPos: Float) {
        _uiState.updateIfRetrieved { it.copy(
            colorSliderPos = newPos,
            hasFieldChanges = true,
        ) }
    }

    fun onDifficultyChange(newDifficulty: Float) {
        _uiState.updateIfRetrieved { it.copy(
            difficulty = newDifficulty,
            hasFieldChanges = true,
        ) }
    }

    fun onSaveClick() : Boolean {
        return _uiState.getIfType<EditProfileUiState.Retrieved>()?.run {
            if (name.isBlank()) {
                viewModelScope.launch {
                    _snackbarEvent.emit("Failed to save profile: Missing Name")
                }
                return@run false
            }

            viewModelScope.launch {
                profileRepository.updateProfile(
                    Profile(
                        id = _loadedProfile.id,
                        name = name,
                        color = Color.fromSlider(colorSliderPos),
                        defaultDifficulty = difficulty.toInt(),
                        sessions = _loadedProfile.sessions,
                    )
                )
            }
            _uiState.updateIfRetrieved { it.copy(hasFieldChanges = true) }

            true
        } ?: error("Can only save if retrieved")
    }


    companion object {

        val PROFILE_ID_KEY = object : CreationExtras.Key<Long> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val profileId = this[PROFILE_ID_KEY] as Long
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer
                val profileRepository = appContainer.profileRepository

                EditProfileViewModel (
                    profileRepository = profileRepository,
                    profileId = profileId,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}