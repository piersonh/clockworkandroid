package com.wordco.clockworkandroid.core.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@Composable
fun DifficultySlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Text(
        textAlign = TextAlign.Left,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        text = label,
        style = TextStyle(
            letterSpacing = 0.02.em // or use TextUnit(value, TextUnitType.Sp)
        )
    )

    Slider(
        value = value,
        steps = 3,
        valueRange = 0f..4f,
        colors = SliderDefaults.colors(
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
            activeTickColor = MaterialTheme.colorScheme.primaryContainer,
            inactiveTickColor = MaterialTheme.colorScheme.primary,
            thumbColor = MaterialTheme.colorScheme.secondary
        ),
        onValueChange = onValueChange
    )
}