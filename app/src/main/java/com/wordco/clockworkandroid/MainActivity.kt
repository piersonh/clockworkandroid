package com.wordco.clockworkandroid

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.Locale

var list = mutableListOf(
    listOf("Assignment", 2660, 33, 3, Color.Green),
    listOf("Project Plan", 30000, 20, 2, Color.Blue),
    listOf("Homework 100", 100, 5, 3, Color.Red)
)
var lato : FontFamily = FontFamily(Font(R.font.lato_light, FontWeight.Light))


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            var count : Int by remember { mutableIntStateOf(0) }
            NavHost(
                navController = navController,
                startDestination = "List"
            ) {
                composable(route = "List") {
                    Scaffold(
                        topBar = {
                            CustomTopBar()
                        },
                        floatingActionButtonPosition = FabPosition.End,
                        floatingActionButton = {
                            TestNavButton("Add", navController,"Add")

                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                            innerPadding ->
                        Box(modifier = Modifier.padding(
                            PaddingValues(top = innerPadding.calculateTopPadding())))
                        { awesomelist(value = count) }

                    }
                }
                composable(route = "Add",
                    enterTransition = {
                        slideIntoContainer(
                            animationSpec = tween(150, easing = LinearEasing),
                            towards = AnimatedContentTransitionScope.SlideDirection.Up
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            animationSpec = tween(150, easing = LinearEasing),
                            towards = AnimatedContentTransitionScope.SlideDirection.Up
                        )
                    }
                )
                {
                    Scaffold(
                        floatingActionButton = { TestNavButton("Back", navController, "list") }
                    ) {
                        Text("PLACEHOLDER TEST", modifier = Modifier.fillMaxSize())
                    }
                }

            }
        }
    }
}

fun click(value: Number) {
    list.add(listOf("Test", 100, 10, 5, Color.Yellow))
    Log.d("Working", "Uhhh meow?")
}

@Composable
private fun HelloContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Hello!",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = { Text("Name") }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CustomTopBar() {
    TopAppBar(
        title = {Text("Your Tasks", fontFamily = lato)}

    )
}
@Preview(showBackground = true)
@Composable
fun awesomelist(value: Number = 0) {
    key(value) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .padding(5.dp)
                .background(color = Color.DarkGray)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())

        ) {
            for (item in list) {
                ListItem(item[0].toString(), item[1], item[2], item[3], item[4])
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
        fontFamily = lato
    )
}

fun convert(time: Int): String {
    val hours = time - time % 3600
    val minutes = ((time - hours) - ((time - hours) % 60))
    //val seconds = (time - hours - minutes)
    return String.format(Locale.getDefault(),"%02d:%02d", hours/3600, minutes/60)
}

@Preview
@Composable
fun ListItem(
    title: String = "Assignment",
    time: Any = 693,
    days: Any = 33,
    diff: Any = 5,
    color: Any = Color.Green,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth(1f)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(color = Color(42,42,42))
            .height(40.dp)
    ) {
        Box(modifier = Modifier
            .background(color = color as Color)
            .fillMaxHeight()
            .width(10.dp)
        )
        Text(title, fontFamily = lato,
            fontSize = 23.sp,
            color = Color.White,
            overflow= TextOverflow.Ellipsis,
            modifier = Modifier
                .width(130.dp)
        )
        Box(modifier = Modifier
            .background(color = Color.Black)
            .fillMaxHeight()
            .width(3.dp)
            .zIndex(4f)
        )
        ClockImage()
        Text(
            convert(time as Int), fontFamily = lato,
            fontSize = 23.sp,
            color = Color.White,
            modifier = Modifier
                .width(65.dp)
        )
        CalImage()
        Text(
            days.toString()
            , fontFamily = lato,
            fontSize = 23.sp,
            color = Color.White,
            modifier = Modifier
                .width(30.dp)
        )
        StarImage()
        Text(
            diff.toString()
            , fontFamily = lato,
            fontSize = 23.sp,
            color = Color.White,
            modifier = Modifier
                .width(30.dp)
        )
    }
}
@Composable
fun TestNavButton(text: String = "Button", navController: NavController, route: String = "list") {
    FloatingActionButton(
        onClick = { navController.navigate(route) }
    ) {
        Text(text)
    }

}
@Composable
fun makeButton(text: String = "!!", selected: Int, function: (Int) -> Unit) {
    FloatingActionButton(
        onClick = { click(selected); function(selected + 1) },
        /*elevation = ButtonDefaults.buttonElevation(),
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .size(50.dp)
            .zIndex(10f)
            .offset(100.dp, 100.dp)
            .aspectRatio(1f),*/
        shape = RoundedCornerShape(100.dp)) {
        Text(text)
    }
}

@Preview
@Composable
fun StarImage() {
    Image(
        painter = painterResource(id = R.drawable.star),
        contentDescription = "Star",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .aspectRatio(0.7f)
    )
}
@Preview
@Composable
fun ClockImage() {
    Image(
        painter = painterResource(id = R.drawable.clock),
        contentDescription = "Time",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .aspectRatio(0.7f)
    )
}
@Preview
@Composable
fun CalImage() {
    Image(
        painter = painterResource(id = R.drawable.cal),
        contentDescription = "Time",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .aspectRatio(0.7f)
    )
}
