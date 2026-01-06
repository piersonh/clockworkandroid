package com.wordco.clockworkandroid.core.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.core.ui.theme.ClockWorkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.AspectRatioPreviews

@Composable
fun ErrorReport(
    header: String,
    message: String,
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
            header,
            fontFamily = LATO,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))

        // TODO: CHECK THIS
        BasicText(
            text = message,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 14.sp,
                maxFontSize = 24.sp
            ),
            style = TextStyle(
                fontFamily = LATO,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 24.sp
            ),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
                .padding(20.dp)
                .heightIn(max=60.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
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
            header = "Failed to Load",
            message = "Womp Womp, Calling the wambulance",
            onCopyErrorInfoClick = {}
        )
    }
}