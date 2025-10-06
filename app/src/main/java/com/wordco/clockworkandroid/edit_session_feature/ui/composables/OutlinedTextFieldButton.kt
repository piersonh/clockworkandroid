package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.em

@Composable
fun OutlinedTextFieldButton (
    value: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    onClick: () -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Box (
        modifier = modifier
    ) {
        OutlinedTextField(
            // override the disabled colors
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = Color.Transparent,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledSuffixColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            value = value,
            enabled = false,
            modifier = Modifier
                .combinedClickable(
                    onClick = onClick
                ),
            label = label?.let {
                {
                    Text(
                        it,
                        style = TextStyle(
                            letterSpacing = 0.02.em, // or use TextUnit(value, TextUnitType.Sp)
                        ),
                        maxLines = 1
                    )
                }
            },
            onValueChange = { },
            singleLine = true,
            readOnly = true,
            trailingIcon = trailingIcon
        )
    }
}