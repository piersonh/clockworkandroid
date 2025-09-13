package com.wordco.clockworkandroid.edit_profile_feature.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.core.domain.util.FakeProfileRepository
import com.wordco.clockworkandroid.core.ui.util.fromSlider
import com.wordco.clockworkandroid.core.ui.util.hue
import com.wordco.clockworkandroid.edit_profile_feature.ui.util.runIfRetrieved
import com.wordco.clockworkandroid.edit_profile_feature.ui.util.updateRetrieved
import kotlinx.coroutines.flow.MutableStateFlow
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

    private lateinit var _loadedProfile: Profile


    init {
        viewModelScope.launch {
            profileRepository.getProfile(profileId).first().run {
                _loadedProfile = this

                _uiState.update {
                    EditProfileUiState.Retrieved(
                        name = name,
                        colorSliderPos = color.hue() / 360,
                        difficulty = defaultDifficulty.toFloat()
                    )
                }
            }
        }
    }


    fun onNameChange(newName: String) {
        _uiState.updateRetrieved {
            it.copy(name = newName)
        }
    }

    fun onColorSliderChange(newPos: Float) {
        _uiState.updateRetrieved { it.copy(colorSliderPos = newPos) }
    }

    fun onDifficultyChange(newDifficulty: Float) {
        _uiState.updateRetrieved { it.copy(difficulty = newDifficulty) }
    }


    sealed interface EditProfileResult {
        data object Success : EditProfileResult
        sealed interface Error : EditProfileResult
        data object MissingName : Error
    }

    fun onSaveClick() : EditProfileResult {
        return _uiState.runIfRetrieved {
            if (name.isBlank()) {
                return@runIfRetrieved EditProfileResult.MissingName
            }

            viewModelScope.launch {
                profileRepository.updateProfile(
                    Profile(
                        id = _loadedProfile.id,
                        name = name,
                        color = Color.fromSlider(colorSliderPos),
                        defaultDifficulty = (4 * difficulty).toInt(),
                        sessions = _loadedProfile.sessions,
                    )
                )
            }
            return@runIfRetrieved EditProfileResult.Success
        }
    }


    companion object {

        val PROFILE_ID_KEY = object : CreationExtras.Key<Long> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                //val taskRepository = (this[APPLICATION_KEY] as MainApplication).taskRepository
                val profileId = this[PROFILE_ID_KEY] as Long
                val profileRepository = FakeProfileRepository.factory()

                EditProfileViewModel (
                    profileRepository = profileRepository,
                    profileId = profileId,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}