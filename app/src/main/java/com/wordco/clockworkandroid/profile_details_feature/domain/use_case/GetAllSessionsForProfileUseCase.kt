package com.wordco.clockworkandroid.profile_details_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.Task
import com.wordco.clockworkandroid.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetAllSessionsForProfileUseCase(
    private val sessionRepository: TaskRepository,
) {
    operator fun invoke(profileId: Long): Flow<List<Task>> {
        return sessionRepository.getSessionsForProfile(profileId)
    }
}