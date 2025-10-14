package com.wordco.clockworkandroid.core.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SessionListItemUiCard(
    stripeColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .drawBehind {
                drawRect(
                    color = backgroundColor,
                )

                drawRect(
                    color = stripeColor,
                    size = Size(width = 10.dp.toPx(), height = size.height)
                )
            }
            .padding(
                start = 20.dp,
                top = 2.dp,
                bottom = 2.dp,
                end = 10.dp,
            )
            .clickable(onClick = onClick),
        content = content,
    )
}