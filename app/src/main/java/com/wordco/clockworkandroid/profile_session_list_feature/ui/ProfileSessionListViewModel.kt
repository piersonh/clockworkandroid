package com.wordco.clockworkandroid.profile_session_list_feature.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.use_case.GetProfileUseCase
import com.wordco.clockworkandroid.profile_session_list_feature.domain.use_case.DeleteProfileUseCase
import com.wordco.clockworkandroid.profile_session_list_feature.domain.use_case.GetAllSessionsForProfileUseCase
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.mapper.toCompletedSessionListItem
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.mapper.toTodoSessionListItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileSessionListViewModel(
    private val profileId: Long,
    getProfileUseCase: GetProfileUseCase,
    getAllSessionsForProfileUseCase: GetAllSessionsForProfileUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileSessionListUiState>(
        ProfileSessionListUiState.Retrieving
    )

    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileSessionListUiEvent>()
    val events = _events.asSharedFlow()

    private val _profile = getProfileUseCase(profileId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(),null)

    private val _sessions = getAllSessionsForProfileUseCase(profileId)
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

                    val (completeSessions, todoSessions) = sessions
                        .partition { it is CompletedTask }

                    ProfileSessionListUiState.Retrieved(
                        profileName = profile.name,
                        profileColor = profile.color,
                        todoSessions = todoSessions.map {
                            it.toTodoSessionListItem()
                        },
                        completeSessions = completeSessions.map {
                            (it as CompletedTask).toCompletedSessionListItem()
                        },
                    )
                }
            }.collect { state ->
                _uiState.update { state }
            }
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            deleteProfileUseCase(profileId)
            _events.emit(ProfileSessionListUiEvent.NavigateBack)
        }
    }


    companion object {

        val PROFILE_ID_KEY = object : CreationExtras.Key<Long> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                //val savedStateHandle = createSavedStateHandle()
                val profileId = this[PROFILE_ID_KEY] as Long
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer

                ProfileSessionListViewModel(
                    profileId = profileId,
                    getProfileUseCase = appContainer.getProfileUseCase,
                    getAllSessionsForProfileUseCase = appContainer.getAllSessionsForProfileUseCase,
                    deleteProfileUseCase = appContainer.deleteProfileUseCase,
                    //savedStateHandle = savedStateHandle
                )
            }
        }
    }
}