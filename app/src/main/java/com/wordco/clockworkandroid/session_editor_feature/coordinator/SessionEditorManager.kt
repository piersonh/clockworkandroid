package com.wordco.clockworkandroid.session_editor_feature.coordinator

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.CompletedTask
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.model.Profile
import com.wordco.clockworkandroid.core.domain.model.StartedTask
import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.use_case.GetProfileUseCase
import com.wordco.clockworkandroid.core.domain.use_case.GetSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.CreateSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetRemindersForSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.UpdateSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate
import com.wordco.clockworkandroid.session_editor_feature.domain.model.DraftValidationError
import com.wordco.clockworkandroid.session_editor_feature.domain.model.ReminderDraft
import com.wordco.clockworkandroid.session_editor_feature.domain.model.SessionDraft
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

sealed class SessionEditorManager(
    private val sessionDraftFactory: SessionDraftFactory,
    private val reminderDraftFactory: ReminderDraftFactory,
) {
    abstract val state: StateFlow<SessionEditorState>

    protected abstract fun updateDraft(
        block: SessionDraft.(currentProfile: Profile?) -> SessionDraft
    )

    protected abstract fun updateReminder(
        block: List<ReminderDraft>.(sessionDraft: SessionDraft) -> List<ReminderDraft>
    )

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
        private val uiCoroutineScope: CoroutineScope,
        private val ioCoroutineScope: CoroutineScope,
        private val sessionDraftFactory: SessionDraftFactory,
        private val reminderDraftFactory: ReminderDraftFactory,
        private val getProfileUseCase: GetProfileUseCase,
        private val createSessionUseCase: CreateSessionUseCase,
    ) : SessionEditorManager(sessionDraftFactory, reminderDraftFactory) {

        private sealed interface InternalState {
            data object Retrieving : InternalState
            data class Error(
                val alert: String,
                val error: Exception,
            ) : InternalState
            data class Retrieved(
                val draft: SessionDraft,
                val activeProfile: Profile?,
                val reminders: List<ReminderDraft>,
                val hasUnsavedChanges: Boolean,
            ) : InternalState
        }

        private val _state = MutableStateFlow<InternalState>(InternalState.Retrieving)
        override val state = _state.map { state ->
            when (state) {
                is InternalState.Error -> SessionEditorState.Error(
                    alert = state.alert,
                    error = state.error,
                )
                is InternalState.Retrieved -> SessionEditorState.Retrieved(
                    draft = state.draft,
                    activeProfile = state.activeProfile,
                    reminders = state.reminders,
                    isEstimateEditable = true,
                    hasUnsavedChanges = state.hasUnsavedChanges,
                )
                InternalState.Retrieving -> SessionEditorState.Retrieving
            }
        }.stateIn(
            scope = uiCoroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = SessionEditorState.Retrieving
        )

        init {
            uiCoroutineScope.launch {
                try {
                    setup(profileId)
                } catch (e: Exception) {
                    _state.update {
                        InternalState.Error(
                            alert = "Failed to initialize editor",
                            error = e,
                        )
                    }
                }
            }
        }

        private suspend fun setup(profileId: Long?) {
            val profile = profileId?.let { getProfileUseCase(it).first() }
            val draft = sessionDraftFactory.createNew(profile)

            val reminders = listOf(reminderDraftFactory.createNew(draft))

            _state.update { InternalState.Retrieved(
                draft = draft,
                reminders = reminders,
                activeProfile = profile,
                hasUnsavedChanges = false,
            ) }
        }

        private fun updateRetrieved(
            block: InternalState.Retrieved.() -> InternalState.Retrieved
        ) {
            (_state.value as? InternalState.Retrieved)?.let { state ->
                val newState = block(state)
                _state.update {
                    newState
                }
            }
        }

        override fun updateDraft(block: SessionDraft.(currentProfile: Profile?) -> SessionDraft) {
            updateRetrieved {
                val newDraft = block(draft, activeProfile)
                copy(
                    draft = newDraft,
                    hasUnsavedChanges = true,
                )
            }
        }

        override fun updateReminder(
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

        override fun updateProfile(newProfileId: Long?) {
            if (newProfileId == (state.value as? SessionEditorState.Retrieved)?.draft?.profileId) {
                return
            }

            uiCoroutineScope.launch {
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
                    )
                }
            }
        }

        override fun save() {
            val state = (state.value as? SessionEditorState.Retrieved) ?: return

            val draft = state.draft
            val newSession = NewTask(
                taskId = draft.sessionId,
                name = draft.sessionName,
                dueDate = draft.dueDateTime?.atZone(ZoneId.systemDefault())?.toInstant(),
                difficulty = draft.difficulty,
                color = Color.hsv(draft.colorHue * 360, 1f, 1f),
                userEstimate = draft.estimate?.toDuration(),
                profileId = draft.profileId,
                appEstimate = null,
            )

            val reminders = state.reminders
            val reminderTimes = reminders.map { draft ->
                draft.scheduledTime.atZone(ZoneId.systemDefault()).toInstant()
            }

            ioCoroutineScope.launch {
                createSessionUseCase(
                    task = newSession,
                    reminderTimes = reminderTimes
                )
            }
        }
    }

    class Edit(
        sessionId: Long,
        private val uiCoroutineScope: CoroutineScope,
        private val ioCoroutineScope: CoroutineScope,
        private val sessionDraftFactory: SessionDraftFactory,
        private val reminderDraftFactory: ReminderDraftFactory,
        private val getSessionUseCase: GetSessionUseCase,
        private val getRemindersForSessionUseCase: GetRemindersForSessionUseCase,
        private val getProfileUseCase: GetProfileUseCase,
        private val updateSessionUseCase: UpdateSessionUseCase,
    ) : SessionEditorManager(sessionDraftFactory, reminderDraftFactory) {

        private sealed interface InternalState {
            data object Retrieving : InternalState
            data class Error(
                val alert: String,
                val error: Exception,
            ) : InternalState
            data class Retrieved(
                val draft: SessionDraft,
                val reminders: List<ReminderDraft>,
                val activeProfile: Profile?,
                val originalSession: Task,
                val hasUnsavedChanges: Boolean,
            ) : InternalState
        }

        private val _state = MutableStateFlow<InternalState>(InternalState.Retrieving)
        override val state = _state.map { state ->
            when (state) {
                is InternalState.Error -> SessionEditorState.Error(
                    alert = state.alert,
                    error = state.error,
                )
                is InternalState.Retrieved -> SessionEditorState.Retrieved(
                    draft = state.draft,
                    activeProfile = state.activeProfile,
                    reminders = state.reminders,
                    isEstimateEditable = state.originalSession is NewTask,
                    hasUnsavedChanges = state.hasUnsavedChanges,
                )
                InternalState.Retrieving -> SessionEditorState.Retrieving
            }
        }.stateIn(
            scope = uiCoroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = SessionEditorState.Retrieving
        )

        init {
            uiCoroutineScope.launch {
                try {
                    setup(sessionId)
                } catch (e: Exception) {
                    _state.update {
                        InternalState.Error(
                            alert = "Failed to initialize editor",
                            error = e,
                        )
                    }
                }
            }
        }

        private suspend fun setup(sessionId: Long) {
            val session = getSessionUseCase(sessionId).first()
            val draft = sessionDraftFactory.createFromExisting(session)

            val profile = session.profileId?.let { getProfileUseCase(it).first() }

            val reminders = getRemindersForSessionUseCase(sessionId).first()
                .map { reminderDraftFactory.createFromExisting(it) }

            _state.update { InternalState.Retrieved(
                draft = draft,
                reminders = reminders,
                originalSession = session,
                activeProfile = profile,
                hasUnsavedChanges = false,
            ) }
        }

        private fun updateRetrieved(
            block: InternalState.Retrieved.() -> InternalState.Retrieved
        ) {
            (_state.value as? InternalState.Retrieved)?.let { state ->
                val newState = block(state)
                _state.update {
                    newState
                }
            }
        }

        override fun updateDraft(block: SessionDraft.(currentProfile: Profile?) -> SessionDraft) {
            updateRetrieved {
                val newDraft = block(draft, activeProfile)
                copy(
                    draft = newDraft,
                    hasUnsavedChanges = true,
                )
            }
        }

        override fun updateReminder(
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

        override fun updateProfile(newProfileId: Long?) {
            if (newProfileId == (state.value as? SessionEditorState.Retrieved)?.draft?.profileId) {
                return
            }

            uiCoroutineScope.launch {
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
            val state = (_state.value as? InternalState.Retrieved)?: return

            val draft = state.draft
            val updatedSession = when(val originalSession = state.originalSession) {
                is NewTask -> originalSession.copy(
                    name = draft.sessionName,
                    dueDate = draft.dueDateTime?.atZone(ZoneId.systemDefault())?.toInstant(),
                    difficulty = draft.difficulty,
                    color = Color.hsv(draft.colorHue * 360, 1f, 1f),
                    userEstimate = draft.estimate?.toDuration(),
                    profileId = draft.profileId,
                )
                is StartedTask -> originalSession.copy(
                    name = draft.sessionName,
                    dueDate = draft.dueDateTime?.atZone(ZoneId.systemDefault())?.toInstant(),
                    difficulty = draft.difficulty,
                    color = Color.hsv(draft.colorHue * 360, 1f, 1f),
                    profileId = draft.profileId,
                )
                is CompletedTask -> originalSession.copy(
                    name = draft.sessionName,
                    dueDate = draft.dueDateTime?.atZone(ZoneId.systemDefault())?.toInstant(),
                    difficulty = draft.difficulty,
                    color = Color.hsv(draft.colorHue * 360, 1f, 1f),
                    profileId = draft.profileId,
                )
            }

            val reminders = state.reminders
            val reminderTimes = reminders.map { draft ->
                draft.scheduledTime.atZone(ZoneId.systemDefault()).toInstant()
            }

            ioCoroutineScope.launch {
                updateSessionUseCase(
                    newSession = updatedSession,
                    reminderTimes = reminderTimes,
                )
            }
        }
    }
}