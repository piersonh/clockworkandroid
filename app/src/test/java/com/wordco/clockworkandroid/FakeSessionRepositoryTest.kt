package com.wordco.clockworkandroid

import androidx.compose.ui.graphics.Color
import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.core.domain.util.FakeSessionRepository
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FakeSessionRepositoryTest {

    @Test
    fun updateSessionTest() = runTest {
        val sessions = listOf(
            NewTask(
                taskId = 1,
                name = "NewTask1",
                dueDate = null,
                difficulty = 1,
                color = Color.Companion.Red,
                userEstimate = null,
                profileId = null,
                appEstimate = null,
            )
        )

        val repo = FakeSessionRepository.Companion.factory(sessions)


        repo.updateTask(
            NewTask(
                taskId = 1,
                name = "NewTask1 updated",
                dueDate = null,
                difficulty = 1,
                color = Color.Companion.Red,
                userEstimate = null,
                profileId = null,
                appEstimate = null,
            )
        )

        TestCase.assertEquals(
            repo.getTask(1).first().name,
            "NewTask1 updated"
        )
    }
}