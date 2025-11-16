package com.wordco.clockworkandroid

import android.app.Application
import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import com.wordco.clockworkandroid.core.data.fake.FakeProfileRepository
import com.wordco.clockworkandroid.core.data.fake.FakeReminderRepository
import com.wordco.clockworkandroid.core.data.fake.FakeSessionRepository
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.database.data.local.AppDatabase
import com.wordco.clockworkandroid.database.data.repository.ProfileRepositoryImpl
import com.wordco.clockworkandroid.database.data.repository.ReminderRepositoryImpl
import com.wordco.clockworkandroid.database.data.repository.TaskRepositoryImpl

class MainApplication : Application() {

    lateinit var appContainer : AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = createProductionContainer(this)

        ProcessLifecycleOwner.get().lifecycle
            .addObserver(appContainer.restoreTimerObserver)
    }


    private fun createProductionContainer(context: Context): AppContainer {
        val db by lazy { AppDatabase.getDatabase(context) }

        return AppContainer(
            context = context,
            sessionRepoFactory = { TaskRepositoryImpl(db.taskDao()) },
            profileRepoFactory = { ProfileRepositoryImpl(db.profileDao()) },
            reminderRepoFactory = { ReminderRepositoryImpl(db.reminderDao()) }
        )
    }

    private fun createFakeContainer(context: Context): AppContainer {
        return AppContainer(
            context = context,
            sessionRepoFactory = { FakeSessionRepository(DummyData.SESSIONS) },
            profileRepoFactory = { FakeProfileRepository(DummyData.PROFILES) },
            reminderRepoFactory = { FakeReminderRepository() },
        )
    }
}