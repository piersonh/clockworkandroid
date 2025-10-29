package com.wordco.clockworkandroid

import com.wordco.clockworkandroid.core.domain.repository.SessionReminderScheduler
import com.wordco.clockworkandroid.core.domain.model.ReminderSchedulingData

/**
 * A fake implementation of SessionReminderScheduler for testing.
 */
class FakeSessionReminderScheduler : SessionReminderScheduler {

    var scheduledData: ReminderSchedulingData? = null
    var cancelledWorkId: String? = null
    var nextWorkIdToReturn = "fake-work-id-123"

    override fun schedule(reminderData: ReminderSchedulingData): String {
        this.scheduledData = reminderData
        return nextWorkIdToReturn
    }

    override fun cancel(workRequestId: String) {
        this.cancelledWorkId = workRequestId
    }

    // Helper to reset state between tests
    fun clear() {
        scheduledData = null
        cancelledWorkId = null
    }
}