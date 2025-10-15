package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.em

@Composable
fun OutlinedTextFieldButton(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isEnabled: Boolean = true,
) {
    Box(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
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
            trailingIcon = trailingIcon,
            enabled = isEnabled,
            readOnly = true,
            singleLine = true,
            interactionSource = remember { MutableInteractionSource() },
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    enabled = isEnabled,
                    onClick = onClick,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = LocalIndication.current
                )
        )
    }
}