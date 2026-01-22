package com.wordco.clockworkandroid.session_editor_feature.ui.reminder_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wordco.clockworkandroid.core.ui.theme.ClockWorkTheme
import com.wordco.clockworkandroid.session_editor_feature.ui.reminder_list.model.ReminderListItem

@Composable
fun ReminderList(
    reminders: List<ReminderListItem>
) {
    LazyColumn {
        items(
            items = reminders,
        ) {
            ReminderListUiItem(
                it,
                modifier = Modifier.clickable {

                }
            )
        }
    }
}


@Preview
@Composable
private fun ReminderListPreview() {
    ClockWorkTheme {
        ReminderList(
            reminders = emptyList()
        )
    }
}