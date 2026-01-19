package com.wordco.clockworkandroid.session_editor_feature.coordinator

import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.use_case.GetProfileUseCase
import com.wordco.clockworkandroid.core.domain.use_case.GetSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetRemindersForSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.session_editor_feature.domain.model.DraftValidationError
import com.wordco.clockworkandroid.session_editor_feature.domain.model.ReminderDraft
import com.wordco.clockworkandroid.session_editor_feature.domain.model.SessionDraft
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

sealed class SessionEditorManager() {
    protected val _state = MutableStateFlow<SessionEditorState>(SessionEditorState.Retrieving)
    val state = _state.asStateFlow()

    protected fun setFailure(
        alert: String,
        error: Exception,
    ) {
        _state.update { SessionEditorState.Error(alert, error) }
    }

    private fun updateDraft(block: SessionDraft.() -> SessionDraft) {
        (state.value as? SessionEditorState.Retrieved)?.let { state ->
            _state.update {
                val newDraft = block(state.draft)
                state.copy(
                    draft = newDraft,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    private fun updateReminder(block: List<ReminderDraft>.() -> List<ReminderDraft>) {
        (state.value as? SessionEditorState.Retrieved)?.let { state ->
            _state.update {
                val newDraft = block(state.reminders)
                state.copy(
                    reminders = newDraft,
                    hasUnsavedChanges = true,
                )
            }
        }
    }

    fun updateName(newName: String) {
        updateDraft {
            copy(
                sessionName = newName
            )
        }
    }
    abstract fun updateProfile(newProfileId: Long?)
    fun updateDifficulty(newDifficulty: Int) {
        updateDraft {
            copy(
                difficulty = newDifficulty,
            )
        }
    }
    fun updateColor(newHue: Float) {
        updateDraft {
            copy(
                colorHue = newHue
            )
        }
    }
    fun updateDueDate(newDate: LocalDate?) {
        updateDraft {
            val dueTime = dueDateTime?.toLocalTime()
            val newDueDateTime = if (dueTime != null && newDate != null) {
                newDate.atTime(dueTime)
            } else {
                null
            }
            copy(
                dueDateTime = newDueDateTime
            )
        }
    }
    fun updateDueTime(newTime: LocalTime) {
        updateDraft {
            if (dueDateTime != null) {
                copy(
                    dueDateTime = dueDateTime.toLocalDate().atTime(newTime)
                )
            } else {
                this
            }
        }
    }
    fun updateEstimate(newEstimate: UserEstimate?) {
        updateDraft {
            copy(
                estimate = newEstimate
            )
        }
    }
    fun updateReminderDate(newDate: LocalDate?) {
        updateReminder {
            if (!isEmpty() && newDate != null) {
                val currentReminderTime = first().scheduledTime.toLocalTime()
                listOf(ReminderDraft(
                    id = 0,
                    scheduledTime = newDate.atTime(currentReminderTime)
                ))
            } else {
                emptyList()
            }
        }
    }
    fun updateReminderTime(newTime: LocalTime) {
        updateReminder {
            if (!isEmpty()) {
                val currentReminderDate = first().scheduledTime.toLocalDate()
                listOf(ReminderDraft(
                    id = 0,
                    scheduledTime = currentReminderDate.atTime(newTime)
                ))
            } else {
                this
            }
        }
    }
    fun validate(): List<DraftValidationError> {
        return (state.value as? SessionEditorState.Retrieved)?.run {
            val errors = mutableListOf<DraftValidationError>()
            if(draft.sessionName.isBlank()) {
                errors.add(DraftValidationError.EmptyName)
            }
            errors
        } ?: listOf(DraftValidationError.EditorNotLoaded)
    }
    abstract fun save()


    class Create(
        profileId: Long?,
        coroutineScope: CoroutineScope,
        private val sessionDraftFactory: SessionDraftFactory,
        private val reminderDraftFactory: ReminderDraftFactory,
        private val getProfileUseCase: GetProfileUseCase,
    ) : SessionEditorManager() {
        init {
            coroutineScope.launch {
                try {
                    setup(profileId)
                } catch (e: Exception) {
                    setFailure(
                        alert = "Failed to initialize editor",
                        error = e
                    )
                }
            }
        }

        private suspend fun setup(profileId: Long?) {
            val profile = profileId?.let { getProfileUseCase(it).first() }
            val draft = sessionDraftFactory.createNew(profile)

            val reminders = listOf(reminderDraftFactory.createNew(draft))

            _state.update { SessionEditorState.Retrieved(
                draft = draft,
                reminders = reminders,
                isEstimateEditable = true,
                hasUnsavedChanges = false,
            ) }
        }

        override fun updateProfile(newProfileId: Long?) {
            TODO("Not yet implemented")
        }

        override fun save() {
            TODO("Not yet implemented")
        }
    }

    class Edit(
        sessionId: Long,
        coroutineScope: CoroutineScope,
        private val sessionDraftFactory: SessionDraftFactory,
        private val reminderDraftFactory: ReminderDraftFactory,
        private val getSessionUseCase: GetSessionUseCase,
        private val getRemindersForSessionUseCase: GetRemindersForSessionUseCase
    ) : SessionEditorManager() {

        init {
            coroutineScope.launch {
                try {
                    setup(sessionId)
                } catch (e: Exception) {
                    setFailure(
                        alert = "Failed to initialize editor",
                        error = e
                    )
                }
            }
        }

        private suspend fun setup(sessionId: Long) {
            val session = getSessionUseCase(sessionId).first()
            val draft = sessionDraftFactory.createFromExisting(session)

            val reminders = getRemindersForSessionUseCase(sessionId).first()
                .map { reminderDraftFactory.createFromExisting(it) }

            _state.update { SessionEditorState.Retrieved(
                draft = draft,
                reminders = reminders,
                isEstimateEditable = session is NewTask,
                hasUnsavedChanges = false,
            ) }
        }

        override fun updateProfile(newProfileId: Long?) {
            TODO("Not yet implemented")
        }

        override fun save() {
            TODO("Not yet implemented")
        }
    }
}