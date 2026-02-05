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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.profile_details_feature.ui.model.TodoSessionListItem

@Composable
fun TodoList(
    todoSessions: List<TodoSessionListItem>,
    onSessionClick: (Long) -> Unit,
    onCreateNewSessionClick: () -> Unit,
    accentColor: Color,
    onAccentColor: Color,
    modifier: Modifier = Modifier,
) {
    if (todoSessions.isEmpty()) {
        return EmptyTodoList(
            onCreateNewSessionClick = onCreateNewSessionClick,
            accentColor = accentColor,
            onAccentColor = onAccentColor,
            modifier = modifier,
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

            item {
                TextButton(
                    onClick = onCreateNewSessionClick,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = accentColor,
                        contentColor = onAccentColor,
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Create New Session",
                        fontFamily = LATO,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                }
            }

            item {
                Spacer(Modifier)
            }

            items(
                items = todoSessions,
                key = { it.id }
            ) { session ->
                TodoSessionListUiItem(
                    session = session,
                    Modifier
                        .fillMaxWidth()
                        .clip(shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .height(IntrinsicSize.Min)
                        .clickable(onClick = { onSessionClick(session.id) })
                )
            }
        }
    }
}