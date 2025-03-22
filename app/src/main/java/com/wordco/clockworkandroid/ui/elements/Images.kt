package com.wordco.clockworkandroid.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    modifier = Modifier.aspectRatio(0.7f)
)


@Preview
@Composable
fun ClockImage() = Image(
    painter = painterResource(id = R.drawable.clock),
    contentDescription = "Time",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f)
)


@Preview
@Composable
fun CalImage() = Image(
    painter = painterResource(id = R.drawable.cal),
    contentDescription = "Time",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f)
)

@Preview
@Composable
fun MarkImage() = Image(
    painter = painterResource(id = R.drawable.bookmark),
    contentDescription = "Time",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f)
)

@Preview
@Composable
fun MoonImage() = Image(
    painter = painterResource(id = R.drawable.moon),
    contentDescription = "Time",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f)
)

@Preview
@Composable
fun MugImage() = Image(
    painter = painterResource(id = R.drawable.mug),
    contentDescription = "Time",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f)
)
@Preview
@Composable
fun ComputerImage() = Image(
    painter = painterResource(id = R.drawable.computer),
    contentDescription = "Time",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f)
)
@Preview
@Composable
fun UserImage() = Image(
    painter = painterResource(id = R.drawable.user),
    contentDescription = "Time",
    contentScale = ContentScale.Fit,
    modifier = Modifier.aspectRatio(0.7f)
)