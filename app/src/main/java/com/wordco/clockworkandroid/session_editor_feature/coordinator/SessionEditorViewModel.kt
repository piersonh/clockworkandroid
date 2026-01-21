package com.wordco.clockworkandroid.session_editor_feature.coordinator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.MainApplication
import com.wordco.clockworkandroid.core.domain.use_case.GetProfileUseCase
import com.wordco.clockworkandroid.core.domain.use_case.GetSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.CreateSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetRemindersForSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.UpdateSessionUseCase
import com.wordco.clockworkandroid.session_editor_feature.ui.main_form.SessionFormViewModel
import com.wordco.clockworkandroid.session_editor_feature.ui.profile_picker.ProfilePickerViewModel
import kotlinx.coroutines.CoroutineScope

class SessionEditorViewModel(
    val sessionEditorMode: SessionEditorMode,
    applicationScope: CoroutineScope,
    sessionDraftFactory: SessionDraftFactory,
    reminderDraftFactory: ReminderDraftFactory,
    getProfileUseCase: GetProfileUseCase,
    getSessionUseCase: GetSessionUseCase,
    getRemindersForSessionUseCase: GetRemindersForSessionUseCase,
    createSessionUseCase: CreateSessionUseCase,
    updateSessionUseCase: UpdateSessionUseCase,
) : ViewModel() {
    private val editorManager = when(sessionEditorMode) {
        is SessionEditorMode.Create -> {
            SessionEditorManager.Create(
                profileId = sessionEditorMode.profileId,
                uiCoroutineScope = viewModelScope,
                ioCoroutineScope = applicationScope,
                sessionDraftFactory = sessionDraftFactory,
                reminderDraftFactory = reminderDraftFactory,
                getProfileUseCase = getProfileUseCase,
                createSessionUseCase = createSessionUseCase,
            )
        }
        is SessionEditorMode.Edit -> {
            SessionEditorManager.Edit(
                sessionId = sessionEditorMode.sessionId,
                uiCoroutineScope = viewModelScope,
                ioCoroutineScope = applicationScope,
                sessionDraftFactory = sessionDraftFactory,
                reminderDraftFactory = reminderDraftFactory,
                getSessionUseCase = getSessionUseCase,
                getRemindersForSessionUseCase = getRemindersForSessionUseCase,
                getProfileUseCase = getProfileUseCase,
                updateSessionUseCase = updateSessionUseCase
            )
        }
    }

    val MainFormFactory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer
            
            SessionFormViewModel(
                editorManager = editorManager,
                getAverageSessionDurationUseCase = appContainer.getAverageSessionDurationUseCase,
                getAverageEstimateErrorUseCase = appContainer.getAverageEstimateErrorUseCase
            )
        }
    }

    val ProfilePickerFactory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer

            ProfilePickerViewModel(
                editorManager = editorManager,
                getAllProfilesUseCase = appContainer.getAllProfilesUseCase
            )
        }
    }

    companion object {
        val EDITOR_MODE_KEY = object : CreationExtras.Key<SessionEditorMode> {}
        
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer
                
                val editorMode = this[EDITOR_MODE_KEY]!!

                SessionEditorViewModel(
                    sessionEditorMode = editorMode,
                    applicationScope = appContainer.applicationScope,
                    sessionDraftFactory = SessionDraftFactory(),
                    reminderDraftFactory = ReminderDraftFactory(),
                    getProfileUseCase = appContainer.getProfileUseCase,
                    getSessionUseCase = appContainer.getSessionUseCase,
                    getRemindersForSessionUseCase = appContainer.getRemindersForSessionUseCase,
                    createSessionUseCase = appContainer.createSessionUseCase,
                    updateSessionUseCase = appContainer.updateSessionUseCase,
                )
            }
        }
    }
}