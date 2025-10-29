package com.wordco.clockworkandroid

import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.CreateReminderUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class CreateReminderUseCaseTest {

    // SUT (System Under Test)
    private lateinit var createReminderUseCase: CreateReminderUseCase

    // Fakes (Dependencies)
    private lateinit var fakeRepository: FakeReminderRepository
    private lateinit var fakeScheduler: FakeSessionReminderScheduler

    @Before
    fun setUp() {
        // Initialize fakes and the SUT before each test
        fakeRepository = FakeReminderRepository()
        fakeScheduler = FakeSessionReminderScheduler()
        createReminderUseCase = CreateReminderUseCase(fakeRepository, fakeScheduler)
    }

    @Test
    fun `invoke() should insert reminder, schedule work, and update reminder with workId`() = runTest {
        // Arrange
        val sessionId = 1L
        val message = "Test reminder"
        val time = Instant.now().plusSeconds(3600) // 1 hour from now

        // Act
        createReminderUseCase(sessionId, message, time)

        // Assert

        // 1. Verify scheduler was called with correct data
        val scheduledData = fakeScheduler.scheduledData
        assertNotNull(scheduledData)
        assertEquals(message, scheduledData?.message)
        assertEquals(time, scheduledData?.scheduledTime)
        assertEquals(sessionId, scheduledData?.sessionId)

        // 2. Get the generated reminderId (which the fake repo gave to the scheduler)
        val generatedReminderId = scheduledData!!.reminderId
        assertTrue(generatedReminderId > 0) // Our fake starts at 1

        // 3. Verify the final reminder in the repository is correct
        val finalReminder = fakeRepository.getReminder(generatedReminderId)
        assertNotNull(finalReminder)
        assertEquals(generatedReminderId, finalReminder?.reminderId)
        assertEquals(sessionId, finalReminder?.sessionId)
        assertEquals(Reminder.Status.PENDING, finalReminder?.status)
        assertEquals(time, finalReminder?.scheduledTime)

        // 4. CRITICAL: Check that the reminder was updated with the workId from the scheduler
        assertEquals(fakeScheduler.nextWorkIdToReturn, finalReminder?.workRequestId)
    }
}