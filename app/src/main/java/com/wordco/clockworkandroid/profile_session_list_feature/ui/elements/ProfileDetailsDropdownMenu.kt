package com.wordco.clockworkandroid.profile_session_list_feature.ui.elements

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.profile_session_list_feature.ui.ProfileDetailsUiEvent

@Composable
fun ProfileDetailsDropdownMenu(
    isMenuExpanded: Boolean,
    onEvent: (ProfileDetailsUiEvent.DetailsEvent) -> Unit,
) {
    DropdownMenu(
        expanded = isMenuExpanded,
        onDismissRequest = { onEvent(ProfileDetailsUiEvent.ModalDismissed) }
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = "Edit",
                    fontSize = 25.sp,
                    textAlign = TextAlign.Right,
                    fontFamily = LATO,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            onClick = {
                onEvent(ProfileDetailsUiEvent.MenuClosed)
                onEvent(ProfileDetailsUiEvent.EditClicked)
            }
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = "Delete",
                    fontSize = 25.sp,
                    textAlign = TextAlign.Right,
                    fontFamily = LATO,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            onClick = {
                onEvent(ProfileDetailsUiEvent.MenuClosed)
                onEvent(ProfileDetailsUiEvent.DeleteClicked)
            }
        )
    }
}