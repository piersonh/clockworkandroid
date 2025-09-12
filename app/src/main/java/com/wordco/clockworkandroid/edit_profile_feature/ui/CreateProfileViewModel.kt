package com.wordco.clockworkandroid.edit_profile_feature.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.core.domain.util.FakeProfileRepository
import com.wordco.clockworkandroid.core.ui.util.fromSlider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class CreateProfileViewModel (
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateProfileUiState(
        name = "",
        colorSliderPos = Random.nextFloat(),
        difficulty = 0f,
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


    sealed interface CreateProfileResult {
        data object Success : CreateProfileResult
        sealed interface Error : CreateProfileResult
        data object MissingName : Error
    }

    fun onCreateProfileClick() : CreateProfileResult {
        with(_uiState.value) {

            if (name.isBlank()) {
                return CreateProfileResult.MissingName
            }

            viewModelScope.launch {
                profileRepository.insertProfile(
                    Profile(
                        id = 0,
                        name = name,
                        color = Color.fromSlider(colorSliderPos),
                        defaultDifficulty = (4 * difficulty).toInt(),
                        sessions = emptyList(),
                    )
                )
            }
        }

        return CreateProfileResult.Success
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                //val taskRepository = (this[APPLICATION_KEY] as MainApplication).taskRepository
                val profileRepository = FakeProfileRepository.factory()

                CreateProfileViewModel (
                    profileRepository = profileRepository
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}