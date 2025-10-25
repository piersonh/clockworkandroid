package com.wordco.clockworkandroid.user_stats_feature.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.ui.composables.SessionListItemUiCard
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.asHHMM
import com.wordco.clockworkandroid.core.ui.util.dpScaledWith
import com.wordco.clockworkandroid.user_stats_feature.ui.model.CompletedSessionListItem
import com.wordco.clockworkandroid.user_stats_feature.ui.util.asDateTime
import java.util.Locale

@Composable
fun CompletedTaskUIListItem(
    task: CompletedSessionListItem,
    backgroundColor: Color,
    onClick: () -> Unit,
) {
    SessionListItemUiCard(
        stripeColor = task.color,
        backgroundColor = backgroundColor,
        onClick = onClick
    ) {
        Text(
            task.name,
            fontFamily = LATO,
            fontSize = 23.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.height(30.dp),
        ) {
            Box(
                modifier = Modifier.size(23.dpScaledWith(23.sp)).padding(2.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.checked_box),
                    contentDescription = "Completed",
                    contentScale = ContentScale.Fit,

                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
            Text(
                String.format(
                    Locale.getDefault(),
                    "%s",
                    task.completedAt.asDateTime()
                ),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically)
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.clock),
                    contentDescription = "Session Time",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(23.dpScaledWith(23.sp)),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
                Text(
                    task.totalTime.asHHMM(),
                    fontFamily = LATO,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bullseye),
                    contentDescription = "Error",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(23.dpScaledWith(23.sp)),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
                Text(
                    String.format(
                        Locale.getDefault(),
                        if (task.error.isNegative) "-%s" else "+%s",
                        task.error.abs().asHHMM()
                    ),
                    fontFamily = LATO,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}