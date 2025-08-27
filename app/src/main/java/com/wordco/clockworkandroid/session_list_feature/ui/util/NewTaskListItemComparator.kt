package com.wordco.clockworkandroid.session_list_feature.ui.util

import com.wordco.clockworkandroid.session_list_feature.ui.model.NewTaskListItem
import java.time.Instant
import java.time.Period

class NewTaskListItemComparator : Comparator<NewTaskListItem> {
    override fun compare(task1: NewTaskListItem, task2: NewTaskListItem): Int {
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