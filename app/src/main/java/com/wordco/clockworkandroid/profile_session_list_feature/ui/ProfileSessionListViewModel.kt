package com.wordco.clockworkandroid.profile_session_list_feature.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.domain.util.FakeProfileRepository
import com.wordco.clockworkandroid.core.domain.util.FakeSessionRepository
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.mapper.toProfileSessionListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileSessionListViewModel(
    private val profileId: Long,
    private val profileRepository: ProfileRepository,
    private val sessionRepository: TaskRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileSessionListUiState>(
        ProfileSessionListUiState.Retrieving
    )

    val uiState = _uiState.asStateFlow()

    private val _profile = profileRepository.getProfile(profileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(),null)

    private val _sessions = sessionRepository.getProfileSessions(profileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(),null)

    init {
        viewModelScope.launch {
            combine(
                _profile,
                _sessions,
            ) { profile, sessions ->
                if (profile == null || sessions == null) {
                    ProfileSessionListUiState.Retrieving
                } else {
                    ProfileSessionListUiState.Retrieved(
                        profileName = profile.name,
                        profileColor = profile.color,
                        sessions = sessions.map {
                            it.toProfileSessionListItem()
                        },
                    )
                }
            }.collect { state ->
                _uiState.update { state }
            }
        }
    }


    companion object {

        val PROFILE_ID_KEY = object : CreationExtras.Key<Long> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                //val sessionRepository = (this[APPLICATION_KEY] as MainApplication).taskRepository
                val profileId = this[PROFILE_ID_KEY] as Long
                val sessionRepository = FakeSessionRepository.factory()
                val profileRepository = FakeProfileRepository.factory()

                ProfileSessionListViewModel (
                    profileId = profileId,
                    profileRepository = profileRepository,
                    sessionRepository = sessionRepository,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}