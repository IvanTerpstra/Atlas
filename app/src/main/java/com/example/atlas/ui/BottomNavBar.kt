package com.example.atlas.ui

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

sealed class NavItem(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : NavItem("dashboard", "Home", Icons.Default.Home)
    object Health : NavItem("health", "Health", Icons.Default.Favorite)
    object Goals : NavItem("goals", "Goals", Icons.Default.CheckCircle)
    object ToDo : NavItem("todo", "ToDo", Icons.Default.List)
    object Workouts : NavItem("workouts", "Workouts", Icons.Default.FitnessCenter)
    object Skillz : NavItem("skillz", "Skillz", Icons.Default.Star)
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem.Dashboard,
        NavItem.Health,
        NavItem.Goals,
        NavItem.ToDo,
        NavItem.Workouts,
        NavItem.Skillz
    )
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}