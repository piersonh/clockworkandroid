package com.wordco.clockworkandroid.core.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.util.fromSlider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    val brush = remember {
        Brush.horizontalGradient(
            listOf(
                Color.hsv(0f, 1f, 1f),
                Color.hsv(60f, 1f, 1f),
                Color.hsv(120f, 1f, 1f),
                Color.hsv(180f, 1f, 1f),
                Color.hsv(240f, 1f, 1f),
                Color.hsv(300f, 1f, 1f),
                Color.hsv(360f, 1f, 1f)
            )
        )
    }
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
        thumb = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp)
                    .background(
                        color = Color.fromSlider(value),
                        shape = RoundedCornerShape(50.dp)
                    )
                    .border(
                        width = 5.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(50.dp)
                    )
            )
        },
        track = {
            Box(
                modifier = Modifier
                    //.padding(4.dp)
                    .background(
                        brush = brush,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .height(10.dp)
                    .fillMaxWidth()
            )
        },
        onValueChange = onValueChange
    )
}


@Preview
@Composable
private fun ColorSliderPreview(){
    ClockworkTheme {
        ColorSlider(
            label = "Preview",
            value = 0.5f,
            onValueChange = {},
        )
    }
}