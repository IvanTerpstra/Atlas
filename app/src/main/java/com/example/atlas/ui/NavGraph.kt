package com.example.atlas.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.atlas.ui.dashboard.DashboardScreen
import com.example.atlas.ui.goals.GoalsScreen
import com.example.atlas.ui.health.HealthScreen
import com.example.atlas.ui.skillz.SkillzScreen
import com.example.atlas.ui.todo.TodoScreen
import com.example.atlas.ui.workout.WorkoutScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen() }
        composable("health") { HealthScreen() }
        composable("goals") { GoalsScreen() }
        composable("todo") { TodoScreen() }
        composable("workouts") { WorkoutScreen() }
        composable("skillz") { SkillzScreen() }
    }
}