package com.wordco.clockworkandroid.profile_session_list_feature.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.ui.theme.LATO

@Composable
fun EmptyTodoList(
    onCreateNewSessionClick: () -> Unit,
    accentColor: Color,
    onAccentColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 25.dp),
                verticalArrangement = Arrangement.spacedBy(
                    20.dp,
                    Alignment.CenterVertically
                )
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Only show the image if the available height is larger than our minimum
                if (this@BoxWithConstraints.maxHeight > 400.dp) {
                    Image(
                        painter = painterResource(id = R.drawable.pencil_writing),
                        contentDescription = "Pencil Writing",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .heightIn(max = 120.dp),
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }


                //Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "You Haven't Made Any Tasks for this Template...",
                        fontFamily = LATO,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 34.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                //Spacer(modifier = Modifier.height(20.dp))

                TextButton(
                    onClick = onCreateNewSessionClick,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = accentColor,
                        contentColor = onAccentColor
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(70.dp)
                        .aspectRatio(4f, true)

                ) {
                    Text(
                        text = "Create New Session",
                        fontFamily = LATO,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}