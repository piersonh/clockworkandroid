package com.wordco.clockworkandroid.user_stats_feature.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.core.ui.composables.ClockImage
import com.wordco.clockworkandroid.core.ui.composables.MugImage
import com.wordco.clockworkandroid.core.ui.composables.CheckedBoxImage
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.asHHMM
import com.wordco.clockworkandroid.user_stats_feature.ui.model.CompletedSessionListItem
import com.wordco.clockworkandroid.user_stats_feature.ui.util.asDateTime

@Composable
fun CompletedTaskUIListItem(
    task: CompletedSessionListItem,
    modifier: Modifier = Modifier
) = Row(
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
) {
    Box(
        modifier = Modifier
            .background(color = task.color)
            .fillMaxHeight()
            .width(10.dp)
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(2.dp)
    )
    {
        Text(
            task.name,
            fontFamily = LATO,
            fontSize = 23.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.height(30.dp),

            ) {
            CheckedBoxImage()
            Text(
                String.format(
                    java.util.Locale.getDefault(),
                    "Completed %s",
                    task.completedAt.asDateTime()
                ),
                fontFamily = LATO,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        )
        {
            ClockImage()
            Text(
                task.workTime.asHHMM(),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.width(65.dp)
            )
            MugImage()
            Text(
                task.breakTime.asHHMM(),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.width(65.dp)
            )
        }
    }
}