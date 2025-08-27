package com.wordco.clockworkandroid.session_list_feature.ui.util

import com.wordco.clockworkandroid.session_list_feature.ui.model.SuspendedTaskListItem

class SuspendedTaskListItemComparator : Comparator<SuspendedTaskListItem> {
    override fun compare(task1: SuspendedTaskListItem?, task2: SuspendedTaskListItem?): Int {
        // Sorted by timestamp of last segment (last run task first)?
        TODO("Not yet implemented")
    }
}