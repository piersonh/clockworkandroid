package com.wordco.clockworkandroid.ui.elements

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RectangleButton(
    onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable (RowScope.() -> Unit)
) = Button(
    onClick = onClick,
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    content = content
)