package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.edit_session_feature.ui.model.ReminderListItem

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
    ClockworkTheme {
        ReminderList(
            reminders = emptyList()
        )
    }
}