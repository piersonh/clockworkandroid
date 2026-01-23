package com.wordco.clockworkandroid.session_report_feature.ui.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.session_report_feature.ui.SessionReportUiEvent

@Composable
fun SessionReportDropdownMenu(
    isMenuExpanded: Boolean,
    onEvent: (SessionReportUiEvent.ReportEvent) -> Unit,
) {
    DropdownMenu(
        expanded = isMenuExpanded,
        onDismissRequest = { onEvent(SessionReportUiEvent.MenuClosed) }
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
                onEvent(SessionReportUiEvent.MenuClosed)
                onEvent(SessionReportUiEvent.EditClicked)
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
                onEvent(SessionReportUiEvent.MenuClosed)
                onEvent(SessionReportUiEvent.DeleteClicked)
            }
        )
    }
}