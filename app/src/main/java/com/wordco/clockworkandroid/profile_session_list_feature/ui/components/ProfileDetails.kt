package com.wordco.clockworkandroid.profile_session_list_feature.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.profile_session_list_feature.ui.ProfileDetailsUiEvent
import com.wordco.clockworkandroid.profile_session_list_feature.ui.ProfileDetailsUiState
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.TabbedScreenItem
import kotlinx.coroutines.CoroutineScope

@Composable
fun ProfileDetails(
    uiState: ProfileDetailsUiState.Retrieved,
    onEvent: (ProfileDetailsUiEvent.DetailsEvent) -> Unit,
    coroutineScope: CoroutineScope,
    accentColor: Color,
    onAccentColor: Color,
) {
    val screens = listOf(
        TabbedScreenItem(
            "To-Do"
        ) {
            TodoList(
                todoSessions = uiState.todoSessions,
                onSessionClick = { id -> onEvent(ProfileDetailsUiEvent.TodoSessionClicked(id)) },
                onCreateNewSessionClick = { onEvent(ProfileDetailsUiEvent.CreateSessionClicked) },
                accentColor = accentColor,
                onAccentColor = onAccentColor,
                modifier = Modifier
                    .padding(horizontal = 5.dp),
            )
        },
        TabbedScreenItem(
            "Complete"
        ) {
            CompletedList(
                completeSessions = uiState.completeSessions,
                onSessionClick = { id -> onEvent(ProfileDetailsUiEvent.CompletedSessionClicked(id)) },
                modifier = Modifier
                    .padding(horizontal = 5.dp)
            )
        },
    )
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box (
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                BasicText(
                    text = uiState.profileName,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 24.sp,
                        maxFontSize = 48.sp
                    ),
                    style = TextStyle(
                        fontFamily = LATO,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 48.sp
                    ),
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .fillMaxWidth()
                        .padding(20.dp)
                        .heightIn(max = 60.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Box (
                    modifier = Modifier
                        .background(color = uiState.profileColor)
                        .fillMaxWidth()
                        .height(10.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Box(
            modifier = Modifier
                .clip(
                    shape = RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 10.dp,
                    )
                )
                .background(MaterialTheme.colorScheme.primary)
        ) {
            TabbedScreen(
                screens = screens,
                coroutineScope = coroutineScope,
                accentColor = accentColor,
            )
        }
    }
}