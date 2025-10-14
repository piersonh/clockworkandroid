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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme

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
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
            activeTickColor = MaterialTheme.colorScheme.primary,
            inactiveTickColor = MaterialTheme.colorScheme.primary
        ),
        onValueChange = onValueChange,
        //modifier = Modifier.padding(horizontal = 24.dp) // to match the color picker track length
    )
}

@Preview
@Composable
private fun DifficultySliderPreview() {
    ClockworkTheme {
        DifficultySlider(
            label = "",
            value = 1f,
            onValueChange = {}
        )
    }
}