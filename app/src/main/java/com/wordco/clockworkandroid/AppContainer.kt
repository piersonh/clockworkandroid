package com.wordco.clockworkandroid

import android.content.Context
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.domain.util.FakeProfileRepository
import com.wordco.clockworkandroid.core.domain.util.FakeSessionRepository
import com.wordco.clockworkandroid.database.data.local.AppDatabase
import com.wordco.clockworkandroid.database.data.repository.ProfileRepositoryImpl
import com.wordco.clockworkandroid.database.data.repository.TaskRepositoryImpl
import com.wordco.clockworkandroid.timer_feature.ui.timer.TimerManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface AppContainer {
    val sessionRepository: TaskRepository
    val profileRepository: ProfileRepository
    val timer: TimerManager

    val permissionRequestSignal: PermissionRequestSignaller
        get() = object : PermissionRequestSignaller {
            private val _steam = MutableSharedFlow<String>()
            override val stream = _steam.asSharedFlow()

            override suspend fun request(permission: String) {
                _steam.emit(permission)
            }
        }
}

class ProductionContainer(context: Context) : AppContainer {
    private val db = AppDatabase.getDatabase(context)

    private val taskDao = db.taskDao()
    override val sessionRepository = TaskRepositoryImpl(taskDao)

    private val profileDao = db.profileDao()
    override val profileRepository = ProfileRepositoryImpl(profileDao)

    override val timer = TimerManager(context)
}

class FakeContainer(context: Context) : AppContainer {
    override val sessionRepository = FakeSessionRepository(DummyData.SESSIONS)

    override val profileRepository = FakeProfileRepository(DummyData.PROFILES)

    override val timer = TimerManager(context)
}