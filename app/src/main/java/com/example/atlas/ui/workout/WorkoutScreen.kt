package com.example.atlas.ui.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atlas.data.models.WorkoutPlan

val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

@Composable
fun WorkoutScreen(viewModel: WorkoutViewModel = viewModel()) {
    val plans by viewModel.workoutPlans.collectAsState()
    var showAddPlan by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddPlan = true },
                containerColor = Color(0xFFA78BFA)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add workout", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Weekly plan", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
            }
            items(daysOfWeek) { day ->
                val dayPlans = plans.filter { it.dayOfWeek == day }
                DaySection(day = day, plans = dayPlans, viewModel = viewModel)
            }
        }
    }

    if (showAddPlan) {
        AddWorkoutDialog(
            onDismiss = { showAddPlan = false },
            onConfirm = { day, name, minutes ->
                viewModel.addPlan(day, name, minutes)
                showAddPlan = false
            }
        )
    }
}

@Composable
fun DaySection(day: String, plans: List<WorkoutPlan>, viewModel: WorkoutViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .padding(end = 0.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (plans.any { it.isCompleted }) Color(0xFF4ADE80)
                        else if (plans.isNotEmpty()) Color(0xFFA78BFA)
                        else Color(0xFF2A2A2E),
                        modifier = Modifier.size(8.dp)
                    ) {}
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(day, style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f))
                if (plans.isEmpty()) {
                    Text("Rest", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (plans.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                plans.forEach { plan ->
                    WorkoutPlanCard(plan = plan, viewModel = viewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun WorkoutPlanCard(plan: WorkoutPlan, viewModel: WorkoutViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var showAddExercise by remember { mutableStateOf(false) }
    val exercises by viewModel.getExercises(plan.id).collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(plan.name, style = MaterialTheme.typography.bodySmall)
                Text("${plan.estimatedMinutes} min",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (plan.isCompleted) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF4ADE80).copy(alpha = 0.15f)
                ) {
                    Text("Done", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall, color = Color(0xFF4ADE80))
                }
            }
            Checkbox(
                checked = plan.isCompleted,
                onCheckedChange = { viewModel.togglePlanComplete(plan) },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4ADE80))
            )
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    if (expanded) Icons.Default.Delete else Icons.Default.Add,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = { viewModel.deletePlan(plan) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete plan",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp))
            }
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(6.dp))
            exercises.forEach { exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("• ${exercise.name}  ${exercise.sets}×${exercise.reps}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.deleteExercise(exercise) },
                        modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete exercise",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp))
                    }
                }
            }
            TextButton(onClick = { showAddExercise = true }) {
                Text("+ Add exercise", color = Color(0xFFA78BFA),
                    style = MaterialTheme.typography.labelSmall)
            }
        }
    }

    if (showAddExercise) {
        AddExerciseDialog(
            onDismiss = { showAddExercise = false },
            onConfirm = { name, sets, reps ->
                viewModel.addExercise(plan.id, name, sets, reps)
                showAddExercise = false
            }
        )
    }
}

@Composable
fun AddWorkoutDialog(onDismiss: () -> Unit, onConfirm: (String, String, Int) -> Unit) {
    var selectedDay by remember { mutableStateOf("Monday") }
    var name by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("45") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add workout") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text("Workout name (e.g. Push Day)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = minutes, onValueChange = { minutes = it },
                    label = { Text("Estimated minutes") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                Text("Day", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Column {
                    daysOfWeek.chunked(3).forEach { rowDays ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            rowDays.forEach { day ->
                                FilterChip(
                                    selected = selectedDay == day,
                                    onClick = { selectedDay = day },
                                    label = { Text(day.take(3),
                                        style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) onConfirm(selectedDay, name, minutes.toIntOrNull() ?: 45)
            }) { Text("Add", color = Color(0xFFA78BFA)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddExerciseDialog(onDismiss: () -> Unit, onConfirm: (String, Int, Int) -> Unit) {
    var name by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("3") }
    var reps by remember { mutableStateOf("10") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add exercise") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text("Exercise name") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = sets, onValueChange = { sets = it },
                    label = { Text("Sets") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = reps, onValueChange = { reps = it },
                    label = { Text("Reps") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) onConfirm(name, sets.toIntOrNull() ?: 3, reps.toIntOrNull() ?: 10)
            }) { Text("Add", color = Color(0xFFA78BFA)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}