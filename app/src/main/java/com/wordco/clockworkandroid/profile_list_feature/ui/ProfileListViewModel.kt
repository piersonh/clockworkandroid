package com.wordco.clockworkandroid.profile_list_feature.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.profile_list_feature.ui.model.mapper.toProfileListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileListViewModel(
    profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileListUiState>(ProfileListUiState.Retrieving)

    val uiState: StateFlow<ProfileListUiState> = _uiState.asStateFlow()

    private val _profiles = profileRepository.getProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(),null)


    init {
        viewModelScope.launch {
            _profiles.map { profiles ->
                if (profiles == null) {
                    ProfileListUiState.Retrieving
                } else {
                    ProfileListUiState.Retrieved(
                        profiles = profiles.map{ it.toProfileListItem() }
                    )
                }
            }.collect { uiState ->
                _uiState.update { uiState }
            }
        }
    }

    companion object {


        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val profileRepository = (this[APPLICATION_KEY] as MainApplication).profileRepository
                //val timer = (this[APPLICATION_KEY] as MainApplication).timer


                ProfileListViewModel (
                    profileRepository = profileRepository,
                    //timer = timer,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}