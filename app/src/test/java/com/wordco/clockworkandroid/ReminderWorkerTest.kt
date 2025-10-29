package com.wordco.clockworkandroid

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.wordco.clockworkandroid.core.domain.model.Reminder
import com.wordco.clockworkandroid.reminder.ReminderWorker
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.time.Duration
import java.time.Instant

class ReminderWorkerTest {

    // Fakes (Dependencies)
    private lateinit var fakeRepository: FakeReminderRepository
    private lateinit var fakeNotificationManager: FakeReminderNotificationManager

    // Mocks
    private lateinit var mockContext: Context
    private lateinit var mockParams: WorkerParameters
    private lateinit var mockApplication: MainApplication
    private lateinit var mockAppContainer: AppContainer

    @Before
    fun setUp() {
        // Initialize fakes
        fakeRepository = FakeReminderRepository()
        fakeNotificationManager = FakeReminderNotificationManager()

        // Set up the mock chain to inject fakes
        mockContext = mock()
        mockApplication = mock()
        mockAppContainer = mock()
        mockParams = mock()

        whenever(mockContext.applicationContext).thenReturn(mockApplication)
        whenever(mockApplication.appContainer).thenReturn(mockAppContainer)
        whenever(mockAppContainer.reminderRepository).thenReturn(fakeRepository)
        whenever(mockAppContainer.reminderNotificationManager).thenReturn(fakeNotificationManager)
    }

    @Test
    fun `doWork() when on time, sends notification and sets status to COMPLETED`() = runTest {
        // Arrange
        val reminderId = 1L
        val message = "Time for your reminder!"
        val notificationId = 123
        val scheduledTime = Instant.now().minusSeconds(30) // 30 seconds ago (within tolerance)

        // Add a PENDING reminder to the fake repo
        fakeRepository.insertReminder(
            Reminder(reminderId, 1L, "work-id", scheduledTime, Reminder.Status.PENDING)
        )

        // Set up worker input data
        val inputData = workDataOf(
            ReminderWorker.KEY_REMINDER_ID to reminderId,
            ReminderWorker.KEY_NOTIFICATION_ID to notificationId,
            ReminderWorker.KEY_REMINDER_MESSAGE to message,
            ReminderWorker.KEY_SCHEDULED_TIME to scheduledTime.toEpochMilli()
        )
        whenever(mockParams.inputData).thenReturn(inputData)

        // Create the worker
        val worker = ReminderWorker(mockContext, mockParams)

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.success(), result)
        assertTrue(fakeNotificationManager.wasNotificationSent)
        assertEquals(message, fakeNotificationManager.lastMessage)
        assertEquals(notificationId, fakeNotificationManager.lastNotificationId)

        val finalReminder = fakeRepository.getReminder(reminderId)
        assertEquals(Reminder.Status.COMPLETED, finalReminder?.status)
    }

    @Test
    fun `doWork() when late, does NOT send notification and sets status to EXPIRED`() = runTest {
        // Arrange
        val reminderId = 2L
        val message = "This is late"
        val notificationId = 456
        // 10 minutes ago, which is outside the 5-minute tolerance
        val scheduledTime = Instant.now().minus(Duration.ofMinutes(10))

        // Add a PENDING reminder to the fake repo
        fakeRepository.insertReminder(
            Reminder(reminderId, 1L, "work-id-2", scheduledTime, Reminder.Status.PENDING)
        )

        // Set up worker input data
        val inputData = workDataOf(
            ReminderWorker.KEY_REMINDER_ID to reminderId,
            ReminderWorker.KEY_NOTIFICATION_ID to notificationId,
            ReminderWorker.KEY_REMINDER_MESSAGE to message,
            ReminderWorker.KEY_SCHEDULED_TIME to scheduledTime.toEpochMilli()
        )
        whenever(mockParams.inputData).thenReturn(inputData)

        // Create the worker
        val worker = ReminderWorker(mockContext, mockParams)

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.success(), result)
        assertFalse(fakeNotificationManager.wasNotificationSent) // Should not send

        val finalReminder = fakeRepository.getReminder(reminderId)
        assertEquals(Reminder.Status.EXPIRED, finalReminder?.status)
    }

    @Test
    fun `doWork() when reminderId is invalid, returns failure`() = runTest {
        // Arrange
        val inputData = workDataOf(
            ReminderWorker.KEY_REMINDER_ID to 0L // Invalid ID
        )
        whenever(mockParams.inputData).thenReturn(inputData)
        val worker = ReminderWorker(mockContext, mockParams)

        // Act
        val result = worker.doWork()

        // Assert
        assertEquals(ListenableWorker.Result.failure(), result)
        assertFalse(fakeNotificationManager.wasNotificationSent)
    }
}