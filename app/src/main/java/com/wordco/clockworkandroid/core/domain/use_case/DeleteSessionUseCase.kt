package com.wordco.clockworkandroid.core.domain.use_case

import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import com.wordco.clockworkandroid.core.domain.repository.SessionReminderScheduler
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository

class DeleteSessionUseCase(
    private val sessionRepository: TaskRepository,
    private val reminderRepository: ReminderRepository,
    private val scheduler: SessionReminderScheduler,
) {
    suspend operator fun invoke(sessionId: Long) {
        scheduler.cancelAllForSession(sessionId)
        reminderRepository.deleteAllRemindersForSession(sessionId)
        sessionRepository.deleteTask(sessionId)
    }
}