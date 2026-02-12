package com.wordco.clockworkandroid.session_list_feature.domain.use_case

import com.wordco.clockworkandroid.core.domain.model.NewTask
import com.wordco.clockworkandroid.session_list_feature.domain.util.NewSessionDueDateComparator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class GetNewSessionComparatorUseCase {
    operator fun invoke(): Flow<Comparator<NewTask>> {
        /*

        Call the GetNewSessionSortSettingUseCase to get the sort setting from the settings repo
         then flatmap the flow to the proper comparator

         */

        return emitEveryInterval(period = 1.minutes).map { now ->
            NewSessionDueDateComparator(now)
        }
    }

    private fun emitEveryInterval(period: Duration) = flow {
        while (true) {
            emit(Instant.now())
            delay(period)
        }
    }
}