package com.wordco.clockworkandroid.session_list_feature.domain.util

import com.wordco.clockworkandroid.core.domain.model.StartedTask
import java.time.Instant
import java.time.Period

/**
 * ORDER:
 * Tasks due in 24 hours
 * Tasks with no due date
 * Tasks due after 24 hours
 */
class SuspendedSessionDueDateComparator(
    now: Instant
) : Comparator<StartedTask> {
    override fun compare(
        s1: StartedTask,
        s2: StartedTask
    ): Int {
        return compareValuesBy(s1, s2,
            // 1. Primary Sort: Which bucket is it in?
            { session -> getPriorityRank(session) },

            // 2. Secondary Sort: If in same bucket, sort by Date
            { session -> session.dueDate },

            // 3. Tie-breaker: Alphabetical
            { session -> session.name }
        )
    }

    private val futureThreshold = now.plus(Period.ofDays(1))

    private fun getPriorityRank(session: StartedTask): Int {
        return when {
            // Rank 0: Urgent (Due within 24 hours)
            session.dueDate != null && session.dueDate.isBefore(futureThreshold) -> 0

            // Rank 1: No Due Date
            session.dueDate == null -> 1

            // Rank 2: Future (Due after 24 hours)
            else -> 2
        }
    }
}