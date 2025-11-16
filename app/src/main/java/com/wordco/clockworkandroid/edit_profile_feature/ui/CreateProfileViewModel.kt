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
import com.wordco.clockworkandroid.core.ui.util.fromSlider
import com.wordco.clockworkandroid.edit_profile_feature.domain.use_case.CreateProfileUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class CreateProfileViewModel (
    private val createProfileUseCase: CreateProfileUseCase,
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
        hasFieldChanges = false,
    ))

    val uiState = _uiState.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()


    fun onNameChange(newName: String) {
        _uiState.update { it.copy(
            name = newName,
            hasFieldChanges = true,
        ) }
    }

    fun onColorSliderChange(newPos: Float) {
        _uiState.update { it.copy(
            colorSliderPos = newPos,
            hasFieldChanges = true,
        ) }
    }

    fun onDifficultyChange(newDifficulty: Float) {
        _uiState.update { it.copy(
            difficulty = newDifficulty,
            hasFieldChanges = true,
        ) }
    }


    fun onSaveClick() : Boolean {
        with(_uiState.value) {

            if (name.isBlank()) {
                viewModelScope.launch {
                    _snackbarEvent.emit("Failed to save template: Missing Name")
                }
                return false
            }

            viewModelScope.launch {
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
        }

        _uiState.update { it.copy(hasFieldChanges = false) }

        return true
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer
                val createProfileUseCase = appContainer.createProfileUseCase

                CreateProfileViewModel (
                    createProfileUseCase = createProfileUseCase
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}