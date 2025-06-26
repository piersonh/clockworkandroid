package com.wordco.clockworkandroid.ui.elements

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CircleButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable (RowScope.() -> Unit)) = Button(
    onClick = onClick,
    shape = CircleShape,
    modifier = modifier.aspectRatio(1f),
    contentPadding = PaddingValues(0.dp),
    content = content
)