package com.wordco.clockworkandroid.profile_session_list_feature.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wordco.clockworkandroid.R
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.core.ui.util.asHHMM
import com.wordco.clockworkandroid.profile_session_list_feature.ui.model.CompletedSessionListItem
import java.time.Duration

@Composable
fun CompletedSessionUiListItem(
    session: CompletedSessionListItem,
    modifier: Modifier = Modifier
) = Row(
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
) {
    Box(
        modifier = Modifier
            .background(color = session.color)
            .fillMaxHeight()
            .width(10.dp)
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.padding(2.dp)
    )
    {
        Text(
            session.name,
            fontFamily = LATO,
            fontSize = 23.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.height(30.dp),

            ) {

            Image(
                painter = painterResource(id = R.drawable.checked_box),
                contentDescription = "Completed",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(18.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )

            Text(
                "Completed",
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
            Image(
                painter = painterResource(id = R.drawable.clock),
                contentDescription = "Work Time",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(23.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )

            Text(
                session.workTime.asHHMM(),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.width(65.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.mug),
                contentDescription = "Break Time",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(23.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )

            Text(
                session.breakTime.asHHMM(),
                fontFamily = LATO,
                fontSize = 23.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.width(65.dp)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ClockworkTheme { 
        CompletedSessionUiListItem(
            session = CompletedSessionListItem(
                id = 0,
                name = "Preview Preview  ",
                color = Color.Red,
                workTime = Duration.ofHours(2).plusMinutes(2),
                breakTime = Duration.ofHours(2).plusMinutes(22)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(10.dp))
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .height(IntrinsicSize.Min)
        )
    }
}