package com.wordco.clockworkandroid.core.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wordco.clockworkandroid.core.ui.model.TopLevelDestination
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.util.FAKE_TOP_LEVEL_DESTINATIONS

@Composable
fun NavBar(
    items: List<TopLevelDestination<out Any>>,
    currentDestination: Any,
    navigateTo: (Any) -> Unit,
) {
    NavigationBar(
        tonalElevation = 5.dp,
    ) {
        items.forEach { destination ->
            NavigationBarItem(
                modifier = Modifier.padding(vertical = 5.dp),
                selected = destination.route == currentDestination,
//                    navController.currentDestination?.hierarchy?.any {
//                    it.hasRoute(
//                        destination.route
//                    )
//                } ?: false,
                label = { Text(destination.label) },
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledIconColor = Color.Gray,
                    disabledTextColor = Color.Gray,
                ),
                onClick = { navigateTo(destination.route) },
                icon = {
                    Icon(
                        painter = painterResource(destination.icon),
                        //tint = TODO(),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                },
                enabled = destination.route != Unit
            )
        }
    }
}

@Preview
@Composable
private fun NavBarPreview() {
    ClockworkTheme { 
        NavBar(
            items = FAKE_TOP_LEVEL_DESTINATIONS,
            currentDestination = Unit,
            navigateTo = {}
        )
    }
}