package com.wordco.clockworkandroid.session_editor_feature.coordinator

import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Profile
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

sealed class SessionEditorManager(
    private val sessionDraftFactory: SessionDraftFactory,
    private val reminderDraftFactory: ReminderDraftFactory,
) {
    protected val _state = MutableStateFlow<SessionEditorState>(SessionEditorState.Retrieving)
    val state = _state.asStateFlow()

    protected fun setFailure(
        alert: String,
        error: Exception,
    ) {
        _state.update { SessionEditorState.Error(alert, error) }
    }

    protected fun updateRetrieved(
        block: SessionEditorState.Retrieved.() -> SessionEditorState.Retrieved
    ) {
        (state.value as? SessionEditorState.Retrieved)?.let { state ->
            val newState = block(state)
            _state.update {
                newState
            }
        }
    }

    protected fun updateDraft(block: SessionDraft.(currentProfile: Profile?) -> SessionDraft) {
        updateRetrieved {
            val newDraft = block(draft, activeProfile)
            copy(
                draft = newDraft,
                hasUnsavedChanges = true,
            )
        }
    }

    protected fun updateReminder(
        block: List<ReminderDraft>.(sessionDraft: SessionDraft) -> List<ReminderDraft>
    ) {
        updateRetrieved {
            val newReminders = block(reminders, draft)
            copy(
                reminders = newReminders,
                hasUnsavedChanges = true,
            )
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
        updateDraft { currentProfile ->
            val dueTime = dueDateTime?.toLocalTime()
            val newDueDateTime = if (newDate == null) {
                null
            } else if (dueTime == null) {
                val default = sessionDraftFactory.getDefaultDueDateTime(currentProfile)
                default.toLocalTime().atDate(newDate)
            } else {
                newDate.atTime(dueTime)
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
        updateReminder { session ->
            if (newDate == null) {
                emptyList()
            } else if (isEmpty()) {
                val newReminderTime = reminderDraftFactory.getDefaultScheduledTime(
                    sessionDueDateTime = session.dueDateTime
                ).toLocalTime()
                listOf(ReminderDraft(
                    id = 0,
                    scheduledTime = newDate.atTime(newReminderTime)
                ))
            } else {
                val currentReminderTime = first().scheduledTime.toLocalTime()
                listOf(ReminderDraft(
                    id = 0,
                    scheduledTime = newDate.atTime(currentReminderTime)
                ))
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
        private val coroutineScope: CoroutineScope,
        private val sessionDraftFactory: SessionDraftFactory,
        private val reminderDraftFactory: ReminderDraftFactory,
        private val getProfileUseCase: GetProfileUseCase,
    ) : SessionEditorManager(sessionDraftFactory, reminderDraftFactory) {
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
                activeProfile = profile,
            ) }
        }

        override fun updateProfile(newProfileId: Long?) {
            if (newProfileId == (state.value as? SessionEditorState.Retrieved)?.draft?.profileId) {
                return
            }

            coroutineScope.launch {
                val newProfile = newProfileId?.let { getProfileUseCase(it).first() }
                updateRetrieved {
                    val oldDefaultName = sessionDraftFactory.getDefaultName(activeProfile)
                    val updatedSessionName = if (draft.sessionName == oldDefaultName) {
                        sessionDraftFactory.getDefaultName(newProfile)
                    } else draft.sessionName

                    val oldDefaultColor = sessionDraftFactory.getDefaultColor(activeProfile)
                    val updatedColor = if (draft.colorHue == oldDefaultColor) {
                        sessionDraftFactory.getDefaultColor(newProfile)
                    } else draft.colorHue

                    val oldDefaultDifficulty = sessionDraftFactory.getDefaultDifficulty(activeProfile)
                    val updatedDifficulty = if (draft.difficulty == oldDefaultDifficulty) {
                        sessionDraftFactory.getDefaultDifficulty(newProfile)
                    } else draft.difficulty

                    val newDraft = draft.copy(
                        sessionName = updatedSessionName,
                        profileId = newProfileId,
                        colorHue = updatedColor,
                        difficulty = updatedDifficulty
                    )

                    copy(
                        draft = newDraft,
                        activeProfile = newProfile,
                        hasUnsavedChanges = true,
                    )
                }
            }
        }

        override fun save() {
            TODO("Not yet implemented")
        }
    }

    class Edit(
        sessionId: Long,
        private val coroutineScope: CoroutineScope,
        private val sessionDraftFactory: SessionDraftFactory,
        private val reminderDraftFactory: ReminderDraftFactory,
        private val getSessionUseCase: GetSessionUseCase,
        private val getRemindersForSessionUseCase: GetRemindersForSessionUseCase,
        private val getProfileUseCase: GetProfileUseCase,
    ) : SessionEditorManager(sessionDraftFactory, reminderDraftFactory) {

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

            val profile = session.profileId?.let { getProfileUseCase(it).first() }

            val reminders = getRemindersForSessionUseCase(sessionId).first()
                .map { reminderDraftFactory.createFromExisting(it) }

            _state.update { SessionEditorState.Retrieved(
                draft = draft,
                reminders = reminders,
                isEstimateEditable = session is NewTask,
                hasUnsavedChanges = false,
                activeProfile = profile,
            ) }
        }

        override fun updateProfile(newProfileId: Long?) {
            if (newProfileId == (state.value as? SessionEditorState.Retrieved)?.draft?.profileId) {
                return
            }

            coroutineScope.launch {
                val newProfile = newProfileId?.let { getProfileUseCase(it).first() }
                updateRetrieved {
                    val newDraft = draft.copy(
                        profileId = newProfileId,
                    )

                    copy(
                        draft = newDraft,
                        activeProfile = newProfile,
                        hasUnsavedChanges = true,
                    )
                }
            }
        }

        override fun save() {
            TODO("Not yet implemented")
        }
    }
}