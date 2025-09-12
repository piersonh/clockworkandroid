package com.wordco.clockworkandroid.core.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wordco.clockworkandroid.core.ui.model.TopLevelDestination

@Composable
fun NavBar(
    items: List<TopLevelDestination<out Any>>,
    currentDestination: Any,
    navigateTo: (Any) -> Unit,
) {
    NavigationBar {
        items.forEach { destination ->
            NavigationBarItem(
                selected = destination.route == currentDestination,
//                    navController.currentDestination?.hierarchy?.any {
//                    it.hasRoute(
//                        destination.route
//                    )
//                } ?: false,
                label = { Text(destination.label) },
                onClick = { navigateTo(destination.route) },
                icon = {
                    Image(
                        painter = painterResource(destination.icon),
                        contentDescription = null,
                        modifier = Modifier.width(50.dp)
                        //modifier = Modifier.aspectRatio(0.7f),
                        //colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                },
            )
        }
    }
}