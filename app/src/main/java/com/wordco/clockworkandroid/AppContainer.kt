package com.wordco.clockworkandroid

import android.content.Context
import android.content.SharedPreferences
import com.wordco.clockworkandroid.core.data.permission.PermissionRequestSignal
import com.wordco.clockworkandroid.core.data.preferences.SharedPreferencesAppPreferencesRepository
import com.wordco.clockworkandroid.core.domain.permission.PermissionRequestSignaller
import com.wordco.clockworkandroid.core.domain.repository.AppPreferencesRepository
import com.wordco.clockworkandroid.core.domain.repository.ProfileRepository
import com.wordco.clockworkandroid.core.domain.repository.ReminderNotificationManager
import com.wordco.clockworkandroid.core.domain.repository.ReminderRepository
import com.wordco.clockworkandroid.core.domain.repository.SessionReminderScheduler
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import com.wordco.clockworkandroid.core.domain.use_case.DeleteSessionUseCase
import com.wordco.clockworkandroid.core.domain.use_case.GetAllProfilesUseCase
import com.wordco.clockworkandroid.core.domain.use_case.GetProfileUseCase
import com.wordco.clockworkandroid.core.domain.use_case.GetSessionUseCase
import com.wordco.clockworkandroid.core.domain.use_case.ManageFirstLaunchUseCase
import com.wordco.clockworkandroid.edit_profile_feature.domain.use_case.CreateProfileUseCase
import com.wordco.clockworkandroid.edit_profile_feature.domain.use_case.UpdateProfileUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.CreateSessionUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetAppEstimateUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetAverageEstimateErrorUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.GetAverageSessionDurationUseCase
import com.wordco.clockworkandroid.edit_session_feature.domain.use_case.UpdateSessionUseCase
import com.wordco.clockworkandroid.profile_session_list_feature.domain.use_case.DeleteProfileUseCase
import com.wordco.clockworkandroid.profile_session_list_feature.domain.use_case.GetAllSessionsForProfileUseCase
import com.wordco.clockworkandroid.reminder.data.ReminderNotificationManagerImpl
import com.wordco.clockworkandroid.reminder.data.SessionReminderSchedulerImpl
import com.wordco.clockworkandroid.reminder.domain.use_case.ProcessScheduledReminderUseCase
import com.wordco.clockworkandroid.session_completion_feature.domain.use_case.CalculateEstimateAccuracyUseCase
import com.wordco.clockworkandroid.session_list_feature.domain.use_case.GetAllTodoSessionsUseCase
import com.wordco.clockworkandroid.timer_feature.data.factory.TimerServiceIntentFactory
import com.wordco.clockworkandroid.timer_feature.data.repository.TimerNotificationActionProviderImpl
import com.wordco.clockworkandroid.timer_feature.data.repository.TimerRepositoryImpl
import com.wordco.clockworkandroid.timer_feature.domain.repository.TimerNotificationManager
import com.wordco.clockworkandroid.timer_feature.domain.use_case.AddMarkerUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.CompleteStartedSessionUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.EndLastSegmentAndStartNewUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.GetActiveSessionIdUseCase
import com.wordco.clockworkandroid.timer_feature.domain.use_case.StartNewSessionUseCase
import com.wordco.clockworkandroid.timer_feature.ui.RestoreTimerObserver
import com.wordco.clockworkandroid.timer_feature.ui.notification.TimerNotificationManagerImpl
import com.wordco.clockworkandroid.user_stats_feature.domain.use_case.GetAllCompletedSessionsUseCase
import com.wordco.clockworkandroid.user_stats_feature.domain.use_case.GetAllSessionsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppContainer(
    context: Context,
    sessionRepoFactory: () -> TaskRepository,
    profileRepoFactory: () -> ProfileRepository,
    reminderRepoFactory: () -> ReminderRepository,
) {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val sessionRepository: TaskRepository by lazy(sessionRepoFactory)
    private val profileRepository: ProfileRepository by lazy(profileRepoFactory)
    private val reminderRepository: ReminderRepository by lazy(reminderRepoFactory)

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

    val getAverageSessionDurationUseCase by lazy {
        GetAverageSessionDurationUseCase(
            sessionRepository = sessionRepository
        )
    }

    val getAverageEstimateErrorUseCase by lazy {
        GetAverageEstimateErrorUseCase(
            sessionRepository = sessionRepository
        )
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

    val getAllSessionsUseCase: GetAllSessionsUseCase by lazy {
        GetAllSessionsUseCase(
            sessionRepository = sessionRepository
        )
    }

    val getAllCompletedSessionsUseCase: GetAllCompletedSessionsUseCase by lazy {
        GetAllCompletedSessionsUseCase(
            sessionRepository = sessionRepository
        )
    }

    val getAllTodoSessionsUseCase: GetAllTodoSessionsUseCase by lazy {
        GetAllTodoSessionsUseCase(
            sessionRepository = sessionRepository
        )
    }

    val getAllSessionsForProfileUseCase: GetAllSessionsForProfileUseCase by lazy {
        GetAllSessionsForProfileUseCase(
            sessionRepository = sessionRepository
        )
    }

    val getActiveSessionIdUseCase: GetActiveSessionIdUseCase by lazy {
        GetActiveSessionIdUseCase(
            sessionRepository = sessionRepository
        )
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

    val createSessionUseCase: CreateSessionUseCase by lazy {
        CreateSessionUseCase(
            sessionRepository = sessionRepository,
            getAppEstimateUseCase = getAppEstimateUseCase,
            reminderRepository = reminderRepository,
            scheduler = reminderScheduler,
        )
    }

    val updateSessionUseCase: UpdateSessionUseCase by lazy {
        UpdateSessionUseCase(
            sessionRepository = sessionRepository,
            getAppEstimateUseCase = getAppEstimateUseCase,
            reminderRepository = reminderRepository,
            scheduler = reminderScheduler,
        )
    }

    val deleteSessionUseCase: DeleteSessionUseCase by lazy {
        DeleteSessionUseCase(
            sessionRepository = sessionRepository,
            reminderRepository = reminderRepository,
            scheduler = reminderScheduler,
        )
    }

    val getProfileUseCase: GetProfileUseCase by lazy {
        GetProfileUseCase(
            profileRepository = profileRepository
        )
    }
    
    val getAllProfilesUseCase: GetAllProfilesUseCase by lazy {
        GetAllProfilesUseCase(
            profileRepository = profileRepository
        )
    }

    val createProfileUseCase: CreateProfileUseCase by lazy {
        CreateProfileUseCase(
            profileRepository = profileRepository
        )
    }

    val updateProfileUseCase: UpdateProfileUseCase by lazy {
        UpdateProfileUseCase(
            profileRepository = profileRepository
        )
    }

    val deleteProfileUseCase: DeleteProfileUseCase by lazy {
        DeleteProfileUseCase(
            profileRepository = profileRepository
        )
    }

    val processScheduledReminderUseCase: ProcessScheduledReminderUseCase by lazy {
        ProcessScheduledReminderUseCase(
            reminderRepository = reminderRepository,
            reminderNotifier = reminderNotificationManager,
        )
    }

    val calculateEstimateAccuracyUseCase: CalculateEstimateAccuracyUseCase by lazy {
        CalculateEstimateAccuracyUseCase()
    }

    val permissionRequestSignal: PermissionRequestSignaller by lazy {
        PermissionRequestSignal()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.applicationContext.getSharedPreferences(
            "ClockworkPrefs",
            Context.MODE_PRIVATE,
        )
    }

    val appPreferencesRepository: AppPreferencesRepository by lazy {
        SharedPreferencesAppPreferencesRepository(sharedPreferences)
    }

    val manageFirstLaunchUseCase: ManageFirstLaunchUseCase by lazy {
        ManageFirstLaunchUseCase(appPreferencesRepository)
    }
}