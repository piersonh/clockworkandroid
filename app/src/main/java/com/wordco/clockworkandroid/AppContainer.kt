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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

interface AppContainer {
    val sessionRepository: TaskRepository
    val profileRepository: ProfileRepository
    val timer: TimerManager

    val permissionRequestSignal: PermissionRequestSignaller
        get() = object : PermissionRequestSignaller {
            private val _requestChannel = Channel<PermissionRequest>()
            override val requestStream = _requestChannel.receiveAsFlow()

            /**
             * The component calls this function. It suspends until the result is available.
             * @return True if the permission was granted, false otherwise.
             */
            override suspend fun request(permission: String): Boolean {
                val request = PermissionRequest(
                    permission = permission,
                    result = CompletableDeferred()
                )
                _requestChannel.send(request)
                return request.result.await() // Suspends here until the result is set
            }
        }
}

class ProductionContainer(context: Context) : AppContainer {
    private val db = AppDatabase.getDatabase(context)

    private val taskDao = db.taskDao()
    override val sessionRepository = TaskRepositoryImpl(taskDao)

    private val profileDao = db.profileDao()
    override val profileRepository = ProfileRepositoryImpl(profileDao)

    override val timer = TimerManager(context,sessionRepository)
}

class FakeContainer(context: Context) : AppContainer {
    override val sessionRepository = FakeSessionRepository(DummyData.SESSIONS)

    override val profileRepository = FakeProfileRepository(DummyData.PROFILES)

    override val timer = TimerManager(context,sessionRepository)
}