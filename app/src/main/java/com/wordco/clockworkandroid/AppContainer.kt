package com.wordco.clockworkandroid

import android.content.Context
import com.wordco.clockworkandroid.core.data.permission.PermissionRequestSignal
import com.wordco.clockworkandroid.core.domain.permission.PermissionRequestSignaller
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.domain.util.FakeProfileRepository
import com.wordco.clockworkandroid.core.domain.util.FakeSessionRepository
import com.wordco.clockworkandroid.database.data.local.AppDatabase
import com.wordco.clockworkandroid.database.data.repository.ProfileRepositoryImpl
import com.wordco.clockworkandroid.database.data.repository.TaskRepositoryImpl
import com.wordco.clockworkandroid.timer_feature.data.factory.TimerServiceIntentFactory
import com.wordco.clockworkandroid.timer_feature.data.repository.TimerNotificationActionProviderImpl
import com.wordco.clockworkandroid.timer_feature.data.repository.TimerRepositoryImpl
import com.wordco.clockworkandroid.timer_feature.domain.repository.TimerNotificationManager
import com.wordco.clockworkandroid.timer_feature.domain.use_case.AddMarkerUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.CompleteStartedSessionUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.EndLastSegmentAndStartNewUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.StartNewSessionUseCase
import com.wordco.clockworkandroid.timer_feature.ui.RestoreTimerObserver
import com.wordco.clockworkandroid.timer_feature.ui.notification.TimerNotificationManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

interface AppContainer {
    val sessionRepository: TaskRepository
    val profileRepository: ProfileRepository
    val timerRepository: TimerRepositoryImpl

    val restoreTimerObserver: RestoreTimerObserver

    val timerNotificationManager: TimerNotificationManager

    val addMarkerUseCase: AddMarkerUseCase
    val endLastSegmentAndStartNewUseCase: EndLastSegmentAndStartNewUseCase
    val startNewSessionUseCase: StartNewSessionUseCase
    val completeStartedSessionUseCase: CompleteStartedSessionUseCase

    val permissionRequestSignal: PermissionRequestSignaller
}

class ProductionContainer(context: Context) : AppContainer {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val db by lazy { AppDatabase.getDatabase(context) }

    private val taskDao by lazy { db.taskDao() }
    override val sessionRepository by lazy { TaskRepositoryImpl(taskDao) }

    private val profileDao by lazy { db.profileDao() }
    override val profileRepository by lazy { ProfileRepositoryImpl(profileDao) }

    private val timerServiceIntentFactory by lazy {
        TimerServiceIntentFactory(context)
    }

    override val timerRepository by lazy { TimerRepositoryImpl(
        context,
        intentFactory = timerServiceIntentFactory
    ) }

    override val restoreTimerObserver by lazy { RestoreTimerObserver(
        sessionRepository = sessionRepository,
        timerRepository = timerRepository,
        externalScope = applicationScope,
    ) }

    private val timerNotificationActionProvider by lazy {
        TimerNotificationActionProviderImpl(
            context = context,
            intentFactory = timerServiceIntentFactory
        )
    }

    override val timerNotificationManager by lazy {
        TimerNotificationManagerImpl(
            context = context,
            permissionSignal = permissionRequestSignal,
            coroutineScope = applicationScope,
            sessionRepository = sessionRepository,
            timerNotificationActionProvider = timerNotificationActionProvider,
        )
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

    override val permissionRequestSignal by lazy {
        PermissionRequestSignal()
    }
}

class FakeContainer(context: Context) : AppContainer {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val sessionRepository by lazy {
        FakeSessionRepository(DummyData.SESSIONS)
    }

    override val profileRepository by lazy {
        FakeProfileRepository(DummyData.PROFILES)
    }

    private val timerServiceIntentFactory by lazy {
        TimerServiceIntentFactory(context)
    }

    override val timerRepository by lazy {
        TimerRepositoryImpl(
            context,
            intentFactory = timerServiceIntentFactory
        )
    }

    override val restoreTimerObserver by lazy { RestoreTimerObserver(
        sessionRepository = sessionRepository,
        timerRepository = timerRepository,
        externalScope = applicationScope,
    ) }

    private val timerNotificationActionProvider by lazy {
        TimerNotificationActionProviderImpl(
            context = context,
            intentFactory = timerServiceIntentFactory
        )
    }

    override val timerNotificationManager by lazy {
        TimerNotificationManagerImpl(
            context = context,
            permissionSignal = permissionRequestSignal,
            coroutineScope = applicationScope,
            sessionRepository = sessionRepository,
            timerNotificationActionProvider = timerNotificationActionProvider
        )
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

    override val permissionRequestSignal by lazy {
        PermissionRequestSignal()
    }
}