package com.wordco.clockworkandroid.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.wordco.clockworkandroid.R

@Preview
@Composable
fun StarImage() = Image(
    painter = painterResource(id = R.drawable.star),
    contentDescription = "Star",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f),
    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
)


@Preview
@Composable
fun RunningImage() = Image(
    painter = painterResource(id = R.drawable.running),
    contentDescription = "Running person",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f),
    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
)



@Preview
@Composable
fun ClockImage() = Image(
    painter = painterResource(id = R.drawable.clock),
    contentDescription = "Time",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f),
    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
)


@Preview
@Composable
fun CalImage() = Image(
    painter = painterResource(id = R.drawable.cal),
    contentDescription = "Date",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f),
    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
)

@Preview
@Composable
fun MarkImage() = Image(
    painter = painterResource(id = R.drawable.bookmark),
    contentDescription = "Time",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f),
    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
)

@Preview
@Composable
fun MoonImage() = Image(
    painter = painterResource(id = R.drawable.moon),
    contentDescription = "Suspend",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f),
    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
)

@Preview
@Composable
fun MugImage() = Image(
    painter = painterResource(id = R.drawable.mug),
    contentDescription = "Break",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f),
    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
)
@Preview
@Composable
fun ComputerImage() = Image(
    painter = painterResource(id = R.drawable.computer),
    contentDescription = "Estimate",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f),
    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
)
@Preview
@Composable
fun UserImage() = Image(
    painter = painterResource(id = R.drawable.user),
    contentDescription = "User",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f),
    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
)

@Preview
@Composable
fun BackImage() = Image(
    painter = painterResource(id = R.drawable.back),
    contentDescription = "Back",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f),
    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
)

@Composable
fun PlusImage(modifier: Modifier) = Image(
    painter = painterResource(id = R.drawable.plus),
    contentDescription = "Add",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f).then(modifier),
    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSecondary)
)