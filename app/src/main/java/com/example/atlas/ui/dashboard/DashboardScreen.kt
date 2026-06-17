package com.example.atlas.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    val tasks by viewModel.tasks.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val healthLogs by viewModel.healthLogs.collectAsState()
    val sleepLogs by viewModel.sleepLogs.collectAsState()
    val workoutPlans by viewModel.workoutPlans.collectAsState()
    val skills by viewModel.skills.collectAsState()

    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11 -> "Good morning, Ivan"
        in 12..17 -> "Good afternoon, Ivan"
        else -> "Good evening, Ivan"
    }
    val dateStr = SimpleDateFormat("EEEE, d MMMM", Locale.ENGLISH).format(calendar.time)

    val todayTasks = tasks.filter { !it.isUpcoming }
    val completedTasks = todayTasks.filter { it.isCompleted }
    val dayOfWeekStr = SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.time)
    val todayWorkouts = workoutPlans.filter {
        it.dayOfWeek.equals(dayOfWeekStr, ignoreCase = true)
    }
    val latestHealth = healthLogs.lastOrNull()
    val latestSleep = sleepLogs.lastOrNull()
    val avgSleep = if (sleepLogs.isNotEmpty())
        sleepLogs.takeLast(7).map { it.hoursSlept }.average().toFloat() else 0f
    val activeGoals = goals.filter { (it.current / it.target.coerceAtLeast(1f)) < 1f }
    val topSkills = skills.sortedByDescending { it.currentLevel }.take(3)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(greeting, style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground)
                    Text(dateStr, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFA78BFA).copy(alpha = 0.15f)
                ) {
                    Text("Atlas", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium, color = Color(0xFFA78BFA))
                }
            }
        }

        item {
            SectionTitle("Today at a glance")
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlanceCard("Tasks", "${completedTasks.size}/${todayTasks.size} done",
                    Color(0xFF818CF8), Modifier.weight(1f))
                GlanceCard("Sleep", if (latestSleep != null) "${latestSleep.hoursSlept}h" else "—",
                    Color(0xFF818CF8), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlanceCard("Calories", if (latestHealth != null) "${latestHealth.calories} kcal" else "—",
                    Color(0xFFF97316), Modifier.weight(1f))
                GlanceCard("Water", if (latestHealth != null) "${latestHealth.waterLiters}L" else "—",
                    Color(0xFF22D3EE), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlanceCard("Protein", if (latestHealth != null) "${latestHealth.protein}g" else "—",
                    Color(0xFF4ADE80), Modifier.weight(1f))
                GlanceCard("Avg sleep 7d", "${String.format(Locale.ENGLISH, "%.1f", avgSleep)}h",
                    Color(0xFFA78BFA), Modifier.weight(1f))
            }
        }

        if (todayWorkouts.isNotEmpty()) {
            item {
                SectionTitle("Today's workout")
                Spacer(modifier = Modifier.height(8.dp))
                todayWorkouts.forEach { plan ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(plan.name, style = MaterialTheme.typography.bodyMedium)
                                Text("${plan.estimatedMinutes} min · ${plan.dayOfWeek}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (plan.isCompleted) Color(0xFF4ADE80).copy(alpha = 0.15f)
                                else Color(0xFFA78BFA).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    if (plan.isCompleted) "Done" else "Pending",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (plan.isCompleted) Color(0xFF4ADE80) else Color(0xFFA78BFA)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        if (todayTasks.isNotEmpty()) {
            item {
                SectionTitle("Today's tasks")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        todayTasks.take(5).forEach { task ->
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = if (task.isCompleted) Color(0xFFA78BFA)
                                    else Color(0xFF2A2A2E),
                                    modifier = Modifier.size(16.dp)
                                ) {}
                                Text(task.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (task.isCompleted)
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f))
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFF818CF8).copy(alpha = 0.15f)
                                ) {
                                    Text(task.category,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF818CF8))
                                }
                            }
                        }
                        if (todayTasks.size > 5) {
                            Text("+ ${todayTasks.size - 5} more tasks",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        if (activeGoals.isNotEmpty()) {
            item {
                SectionTitle("Active goals")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        activeGoals.take(4).forEach { goal ->
                            val progress = (goal.current / goal.target.coerceAtLeast(1f)).coerceIn(0f, 1f)
                            val pct = (progress * 100).toInt()
                            val color = when {
                                pct >= 75 -> Color(0xFF4ADE80)
                                pct >= 40 -> Color(0xFFA78BFA)
                                else -> Color(0xFFF97316)
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(goal.title, style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.weight(1f))
                                    Text("$pct%", style = MaterialTheme.typography.labelSmall,
                                        color = color)
                                }
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth().height(4.dp),
                                    color = color,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        if (topSkills.isNotEmpty()) {
            item {
                SectionTitle("Top skills")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        topSkills.forEach { skill ->
                            val progress = (skill.currentLevel.toFloat() /
                                    skill.targetLevel.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(skill.title, style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.weight(1f))
                                    Text("${skill.currentLevel}/${skill.targetLevel}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFA78BFA))
                                }
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth().height(4.dp),
                                    color = Color(0xFFA78BFA),
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
fun GlanceCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, color = color)
        }
    }
}