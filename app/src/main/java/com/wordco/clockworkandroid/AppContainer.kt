package com.wordco.clockworkandroid

import android.content.Context
import com.wordco.clockworkandroid.core.data.permission.PermissionRequestSignal
import com.wordco.clockworkandroid.core.domain.permission.PermissionRequestSignaller
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.core.domain.repository.ReminderNotificationManager
import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import com.wordco.clockworkandroid.core.domain.repository.SessionReminderScheduler
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.domain.use_case.DeleteSessionUseCase
import com.wordco.clockworkandroid.core.domain.util.DummyData
import com.wordco.clockworkandroid.core.domain.util.FakeProfileRepository
import com.wordco.clockworkandroid.core.domain.util.FakeSessionRepository
import com.wordco.clockworkandroid.database.data.local.AppDatabase
import com.wordco.clockworkandroid.database.data.repository.ProfileRepositoryImpl
import com.wordco.clockworkandroid.database.data.repository.ReminderRepositoryImpl
import com.wordco.clockworkandroid.database.data.repository.TaskRepositoryImpl
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetAppEstimateUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.InsertNewSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.UpdateSessionUseCase
import com.wordco.clockworkandroid.reminder.ReminderNotificationManagerImpl
import com.wordco.clockworkandroid.reminder.SessionReminderSchedulerImpl
import com.wordco.clockworkandroid.session_completion_feature.domain.use_case.CalculateEstimateAccuracyUseCase
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

abstract class AppContainer(context: Context) {

    protected val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    abstract val sessionRepository: TaskRepository
    abstract val profileRepository: ProfileRepository

    abstract val reminderRepository: ReminderRepository

    private val timerServiceIntentFactory by lazy {
        TimerServiceIntentFactory(context)
    }

    private val timerNotificationActionProvider by lazy {
        TimerNotificationActionProviderImpl(
            context = context,
            intentFactory = timerServiceIntentFactory
        )
    }

    private val getAppEstimateUseCase by lazy {
        GetAppEstimateUseCase()
    }

    val timerRepository: TimerRepositoryImpl by lazy {
        TimerRepositoryImpl(
            context,
            intentFactory = timerServiceIntentFactory
        )
    }

    val restoreTimerObserver: RestoreTimerObserver by lazy {
        RestoreTimerObserver(
            sessionRepository = sessionRepository,
            timerRepository = timerRepository,
            externalScope = applicationScope,
        )
    }

    val timerNotificationManager: TimerNotificationManager by lazy {
        TimerNotificationManagerImpl(
            context = context,
            permissionSignal = permissionRequestSignal,
            coroutineScope = applicationScope,
            sessionRepository = sessionRepository,
            timerNotificationActionProvider = timerNotificationActionProvider,
        )
    }

    val reminderNotificationManager: ReminderNotificationManager by lazy {
        ReminderNotificationManagerImpl(
            context = context,
            permissionSignal = permissionRequestSignal,
            coroutineScope = applicationScope,
        )
    }

    val reminderScheduler: SessionReminderScheduler by lazy {
        SessionReminderSchedulerImpl(
            context = context
        )
    }

    val getSessionUseCase: GetSessionUseCase by lazy {
        GetSessionUseCase(sessionRepository = sessionRepository)
    }

    val addMarkerUseCase: AddMarkerUseCase by lazy {
        AddMarkerUseCase(sessionRepository = sessionRepository)
    }

    val endLastSegmentAndStartNewUseCase: EndLastSegmentAndStartNewUseCase by lazy {
        EndLastSegmentAndStartNewUseCase(sessionRepository = sessionRepository)
    }

    val startNewSessionUseCase: StartNewSessionUseCase by lazy {
        StartNewSessionUseCase(sessionRepository = sessionRepository)
    }

    val completeStartedSessionUseCase: CompleteStartedSessionUseCase by lazy {
        CompleteStartedSessionUseCase(
            sessionRepository = sessionRepository,
            reminderRepository = reminderRepository,
            scheduler = reminderScheduler,
        )
    }

    val insertNewSessionUseCase: InsertNewSessionUseCase by lazy {
        InsertNewSessionUseCase(
            sessionRepository = sessionRepository,
            getAppEstimateUseCase = getAppEstimateUseCase,
        )
    }

    val updateSessionUseCase: UpdateSessionUseCase by lazy {
        UpdateSessionUseCase(
            sessionRepository = sessionRepository,
            getAppEstimateUseCase = getAppEstimateUseCase,
        )
    }

    val deleteSessionUseCase: DeleteSessionUseCase by lazy {
        DeleteSessionUseCase(
            sessionRepository = sessionRepository,
            reminderRepository = reminderRepository,
            scheduler = reminderScheduler,
        )
    }

    val calculateEstimateAccuracyUseCase: CalculateEstimateAccuracyUseCase by lazy {
        CalculateEstimateAccuracyUseCase()
    }

    val permissionRequestSignal: PermissionRequestSignaller by lazy {
        PermissionRequestSignal()
    }
}

class ProductionContainer(context: Context) : AppContainer(context) {

    private val db by lazy { AppDatabase.getDatabase(context) }

    private val taskDao by lazy { db.taskDao() }
    override val sessionRepository by lazy { TaskRepositoryImpl(taskDao) }

    private val profileDao by lazy { db.profileDao() }
    override val profileRepository by lazy { ProfileRepositoryImpl(profileDao) }

    private val reminderDao by lazy {db.reminderDao()}
    override val reminderRepository by lazy { ReminderRepositoryImpl(reminderDao) }
}

class FakeContainer(context: Context) : AppContainer(context) {

    override val sessionRepository by lazy {
        FakeSessionRepository(DummyData.SESSIONS)
    }

    override val profileRepository by lazy {
        FakeProfileRepository(DummyData.PROFILES)
    }

    override val reminderRepository: ReminderRepository
        get() = TODO("Not yet implemented")
}