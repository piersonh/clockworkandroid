package com.wordco.clockworkandroid.profile_details_feature.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.wordco.clockworkandroid.profile_details_feature.ui.model.CompletedSessionListItem

@Composable
fun CompletedList(
    completeSessions: List<CompletedSessionListItem>,
    onSessionClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (completeSessions.isEmpty()) {
        return EmptyCompletedList(
            modifier = modifier
        )
    }

    Box(
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            item {
                Spacer(Modifier.height(5.dp))
            }

            items(
                items = completeSessions,
                key = { it.id }
            ) { session ->
                CompletedSessionUiListItem(
                    session = session,
                    Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .height(IntrinsicSize.Min)
                        .clickable(onClick = { onSessionClick(session.id) })
                )
            }
        }
    }
}