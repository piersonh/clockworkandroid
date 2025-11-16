package com.wordco.clockworkandroid

import com.wordco.clockworkandroid.core.domain.repository.SessionReminderScheduler
import com.wordco.clockworkandroid.core.domain.model.ReminderSchedulingData

/**
 * A fake implementation of SessionReminderScheduler for testing.
 */
class FakeSessionReminderScheduler : SessionReminderScheduler {

    var scheduledData: ReminderSchedulingData? = null
    var cancelledWorkId: String? = null

    override fun schedule(reminderData: ReminderSchedulingData) {
        this.scheduledData = reminderData
    }

    override fun cancel(workRequestId: String) {
        this.cancelledWorkId = workRequestId
    }

    override fun cancelAllForSession(sessionId: Long) {
        TODO("Not yet implemented")
    }

    // Helper to reset state between tests
    fun clear() {
        scheduledData = null
        cancelledWorkId = null
    }
}