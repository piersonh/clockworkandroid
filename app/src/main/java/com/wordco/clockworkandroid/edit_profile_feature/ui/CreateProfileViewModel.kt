package com.wordco.clockworkandroid.edit_profile_feature.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.core.ui.util.Fallible
import com.wordco.clockworkandroid.core.ui.util.fromSlider
import com.wordco.clockworkandroid.edit_profile_feature.ui.model.Modal
import com.wordco.clockworkandroid.edit_profile_feature.ui.model.SaveProfileError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class CreateProfileViewModel (
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _fieldDefaults = object : ProfileFormUiState {
        override val name: String = ""
        override val colorSliderPos: Float = Random.nextFloat()
        override val difficulty: Float = 0f

    }

    private val _uiState = MutableStateFlow(CreateProfileUiState(
        name = _fieldDefaults.name,
        colorSliderPos = _fieldDefaults.colorSliderPos,
        difficulty = _fieldDefaults.difficulty,
        currentModal = null
    ))

    val uiState = _uiState.asStateFlow()


    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onColorSliderChange(newPos: Float) {
        _uiState.update { it.copy(colorSliderPos = newPos) }
    }

    fun onDifficultyChange(newDifficulty: Float) {
        _uiState.update { it.copy(difficulty = newDifficulty) }
    }

    private fun hasUserChangedFields() : Boolean {
        return _uiState.value.run {
            (name != _fieldDefaults.name)
                .or(colorSliderPos != _fieldDefaults.colorSliderPos)
                .or(difficulty != _fieldDefaults.difficulty)
        }
    }

    fun onShowDiscardAlert() : Boolean {
        return hasUserChangedFields().also { hasUserChangedFields ->
            if (hasUserChangedFields) {
                _uiState.update { it.copy(currentModal = Modal.Discard) }
            }
        }
    }

    fun onDismissModal() {
        _uiState.update { it.copy(currentModal = null) }
    }


    fun onSaveClick() : Fallible<SaveProfileError> {
        with(_uiState.value) {

            if (name.isBlank()) {
                return Fallible.Error(SaveProfileError.MISSING_NAME)
            }

            viewModelScope.launch {
                profileRepository.insertProfile(
                    Profile(
                        id = 0,
                        name = name,
                        color = Color.fromSlider(colorSliderPos),
                        defaultDifficulty = difficulty.toInt(),
                        sessions = emptyList(),
                    )
                )
            }
        }

        return Fallible.Success
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val profileRepository = (this[APPLICATION_KEY] as MainApplication).profileRepository

                CreateProfileViewModel (
                    profileRepository = profileRepository
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}