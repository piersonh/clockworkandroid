package com.wordco.clockworkandroid.core.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.wordco.clockworkandroid.core.ui.theme.LATO

@Composable
internal fun DiscardAlert(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(text = "Changes Not Saved")
        },

        text = { Text("Are sure you want to leave without saving your changes?") },

        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    "Discard Changes",
                    fontFamily = LATO,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    "Cancel",
                    fontFamily = LATO,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    )
}