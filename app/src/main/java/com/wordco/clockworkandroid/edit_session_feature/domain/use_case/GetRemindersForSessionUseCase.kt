package com.wordco.clockworkandroid.edit_session_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow

class GetRemindersForSessionUseCase(
    private val reminderRepository: ReminderRepository,
) {
    operator fun invoke(sessionId: Long): Flow<List<Reminder>> {
        return reminderRepository.getRemindersForSession(sessionId)
    }
}