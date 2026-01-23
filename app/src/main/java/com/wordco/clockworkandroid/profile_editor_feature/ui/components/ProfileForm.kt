package com.wordco.clockworkandroid.profile_editor_feature.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.wordco.clockworkandroid.core.ui.composables.ColorSlider
import com.wordco.clockworkandroid.core.ui.composables.DifficultySlider
import com.wordco.clockworkandroid.profile_editor_feature.ui.ProfileEditorUiEvent
import com.wordco.clockworkandroid.profile_editor_feature.ui.ProfileEditorUiState

@Composable
fun ProfileForm(
    uiState: ProfileEditorUiState.Retrieved,
    modifier: Modifier = Modifier,
    onEvent: (ProfileEditorUiEvent) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                focusedIndicatorColor = MaterialTheme.colorScheme.secondary
            ),
            value = uiState.name,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { newName -> onEvent(ProfileEditorUiEvent.NameChanged(newName)) },
            label = {
                Text(
                    "Template Name", style = TextStyle(
                        letterSpacing = 0.02.em // or use TextUnit(value, TextUnitType.Sp)
                    )
                )
            }
        )

        ColorSlider(
            label = "Template Color",
            value = uiState.colorSliderPos,
            onValueChange = { newVal -> onEvent(ProfileEditorUiEvent.ColorSliderChanged(newVal)) },
        )

        DifficultySlider(
            label = "Default Session Difficulty",
            value = uiState.difficulty,
            onValueChange = { newVal -> onEvent(ProfileEditorUiEvent.DifficultySliderChanged(newVal)) },
        )
    }
}