package com.wordco.clockworkandroid.ui

import java.time.Instant
import java.time.Period

class UpcomingTaskListItemComparator : Comparator<UpcomingTaskListItem> {
    override fun compare(task1: UpcomingTaskListItem, task2: UpcomingTaskListItem): Int {
        // ORDER:
        // Tasks due in 24 hours
        // Tasks with no due date
        // Tasks due after 24 hours

        // TODO: RENAME (24HoursFromNow)
        val hoursFromNow = Instant.now().plus(Period.ofDays(1))

        return if (task1.dueDate == task2.dueDate) {
            task1.name.compareTo(task2.name)
        } else if (task1.dueDate == null) {
            if (task2.dueDate!!.isAfter(hoursFromNow)) -1 else 1
        } else if (task2.dueDate == null) {
            if (task1.dueDate.isAfter(hoursFromNow)) 1 else -1
        } else {
            task1.dueDate.compareTo(task2.dueDate)
        }
    }

}