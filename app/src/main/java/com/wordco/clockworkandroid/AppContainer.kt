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
import com.wordco.clockworkandroid.timer_feature.data.TimerRepositoryImpl
import com.wordco.clockworkandroid.timer_feature.domain.use_case.AddMarkerUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.CompleteStartedSessionUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.EndLastSegmentAndStartNewUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.StartNewSessionUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

interface AppContainer {
    val sessionRepository: TaskRepository
    val profileRepository: ProfileRepository
    val timerRepository: TimerRepositoryImpl

    val addMarkerUseCase: AddMarkerUseCase
    val endLastSegmentAndStartNewUseCase: EndLastSegmentAndStartNewUseCase
    val startNewSessionUseCase: StartNewSessionUseCase
    val completeStartedSessionUseCase: CompleteStartedSessionUseCase

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
    private val db by lazy { AppDatabase.getDatabase(context) }

    private val taskDao by lazy { db.taskDao() }
    override val sessionRepository by lazy { TaskRepositoryImpl(taskDao) }

    private val profileDao by lazy { db.profileDao() }
    override val profileRepository by lazy { ProfileRepositoryImpl(profileDao) }

    override val timerRepository by lazy { TimerRepositoryImpl(context, sessionRepository) }

    override val addMarkerUseCase by lazy {
        AddMarkerUseCase()
    }
    override val endLastSegmentAndStartNewUseCase by lazy {
        EndLastSegmentAndStartNewUseCase(
            sessionRepository = sessionRepository
        )
    }
    override val startNewSessionUseCase by lazy {
        StartNewSessionUseCase(
            sessionRepository = sessionRepository
        )
    }
    override val completeStartedSessionUseCase by lazy {
        CompleteStartedSessionUseCase(
            sessionRepository = sessionRepository
        )
    }
}

class FakeContainer(context: Context) : AppContainer {
    override val sessionRepository by lazy {
        FakeSessionRepository(DummyData.SESSIONS)
    }

    override val profileRepository by lazy {
        FakeProfileRepository(DummyData.PROFILES)
    }

    override val timerRepository by lazy {
        TimerRepositoryImpl(context,sessionRepository)
    }

    override val addMarkerUseCase by lazy {
        AddMarkerUseCase()
    }
    override val endLastSegmentAndStartNewUseCase by lazy {
        EndLastSegmentAndStartNewUseCase(
            sessionRepository = sessionRepository
        )
    }
    override val startNewSessionUseCase by lazy {
        StartNewSessionUseCase(
            sessionRepository = sessionRepository
        )
    }
    override val completeStartedSessionUseCase by lazy {
        CompleteStartedSessionUseCase(
            sessionRepository = sessionRepository
        )
    }
}