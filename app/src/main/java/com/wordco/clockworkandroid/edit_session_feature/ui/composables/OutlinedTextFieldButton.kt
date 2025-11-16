package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.em
import kotlinx.coroutines.flow.filterIsInstance

@Composable
fun OutlinedTextFieldButton(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isEnabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(interactionSource, isEnabled) {
        if (isEnabled) {
            interactionSource.interactions
                // release event = the field was clicked and the
                //  event wasn't consumed by the trailing icon
                .filterIsInstance<PressInteraction.Release>()
                .collect {
                    onClick()

                    // this interaction focuses the box which makes the label and
                    //  border disappear
                    focusManager.clearFocus()
                }
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = modifier.fillMaxWidth(),
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
        interactionSource = interactionSource,
    )
}