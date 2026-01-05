package com.wordco.clockworkandroid.edit_profile_feature.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.core.ui.composables.AccentRectangleTextButton
import com.wordco.clockworkandroid.core.ui.theme.ClockWorkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews
import com.wordco.clockworkandroid.edit_profile_feature.ui.ProfileFormUiState

@Composable
fun ErrorReport(
    state: ProfileFormUiState.Error,
    modifier: Modifier = Modifier,
    onCopyErrorInfoClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            ":(",
            fontFamily = LATO,
            fontWeight = FontWeight.Bold,
            fontSize = 96.sp,
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            state.header,
            fontFamily = LATO,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            state.message,
            fontFamily = LATO,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(50.dp))
        AccentRectangleTextButton(
            onClick = onCopyErrorInfoClick,
            maxHeight = 56.dp,
        ) {
            Text(
                "Copy Error Info",
                fontFamily = LATO,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
            )
        }
    }
}

@AspectRatioPreviews
@Composable
private fun ErrorReportPreview() {
    ClockWorkTheme {
        ErrorReport(
            state = ProfileFormUiState.Error(
                title = "Preview",
                header = "Failed to load",
                message = "message here",
            ),
            onCopyErrorInfoClick = {},
        )
    }
}