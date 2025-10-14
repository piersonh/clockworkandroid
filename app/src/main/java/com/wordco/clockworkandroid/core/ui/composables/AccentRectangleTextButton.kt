package com.wordco.clockworkandroid.core.ui.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AccentRectangleTextButton(
    onClick: () -> Unit,
    maxHeight: Dp = 70.dp,
    minHeight: Dp = 25.dp,
    aspectRatio: Float = 4f,
    content: @Composable (RowScope.() -> Unit)
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .heightIn(minHeight, maxHeight)
            .aspectRatio(aspectRatio,true),
        content = content,
    )
}