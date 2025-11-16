package com.wordco.clockworkandroid.timer_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.repository.TaskRepository

class GetActiveSessionIdUseCase(
    private val sessionRepository: TaskRepository,
) {
    suspend operator fun invoke(): Long? {
        return sessionRepository.getActiveTaskId()
    }
}