package com.wordco.clockworkandroid.session_list_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.TimerState
import com.wordco.clockworkandroid.core.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow

class GetTimerStateUseCase(
    private val timerRepository: TimerRepository,
) {
    operator fun invoke(): Flow<TimerState> {
        return timerRepository.state
    }
}