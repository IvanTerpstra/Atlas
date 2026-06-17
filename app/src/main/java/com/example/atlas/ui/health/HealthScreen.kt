package com.example.atlas.ui.health

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
import com.example.atlas.data.models.HealthLog
import com.example.atlas.data.models.SleepLog

@Composable
fun HealthScreen(viewModel: HealthViewModel = viewModel()) {
    val healthLogs by viewModel.healthLogs.collectAsState()
    val sleepLogs by viewModel.sleepLogs.collectAsState()
    var showHealthDialog by remember { mutableStateOf(false) }
    var showSleepDialog by remember { mutableStateOf(false) }

    val todayHealth = healthLogs.lastOrNull()
    val avgSleep = if (sleepLogs.isNotEmpty())
        sleepLogs.takeLast(7).map { it.hoursSlept }.average().toFloat() else 0f

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallFloatingActionButton(
                    onClick = { showSleepDialog = true },
                    containerColor = Color(0xFF818CF8)
                ) { Text("💤", style = MaterialTheme.typography.bodyMedium) }
                FloatingActionButton(
                    onClick = { showHealthDialog = true },
                    containerColor = Color(0xFFA78BFA)
                ) { Icon(Icons.Default.Add, contentDescription = "Log health", tint = Color.White) }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("Avg sleep", "${String.format("%.1f", avgSleep)}h",
                        Color(0xFF818CF8), Modifier.weight(1f))
                    StatCard("Calories", "${todayHealth?.calories ?: 0} kcal",
                        Color(0xFFF97316), Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("Protein", "${todayHealth?.protein ?: 0}g",
                        Color(0xFF4ADE80), Modifier.weight(1f))
                    StatCard("Water", "${todayHealth?.waterLiters ?: 0f}L",
                        Color(0xFF22D3EE), Modifier.weight(1f))
                }
            }
            if (sleepLogs.isNotEmpty()) {
                item {
                    Text("Sleep logs", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(sleepLogs.takeLast(7).reversed()) { log ->
                    SleepCard(log, viewModel)
                }
            }
            if (healthLogs.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Nutrition logs", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(healthLogs.takeLast(7).reversed()) { log ->
                    HealthLogCard(log, viewModel)
                }
            }
        }
    }

    if (showHealthDialog) {
        LogHealthDialog(
            onDismiss = { showHealthDialog = false },
            onConfirm = { calories, protein, carbs, fat, water ->
                viewModel.logHealth(calories, protein, carbs, fat, water)
                showHealthDialog = false
            }
        )
    }

    if (showSleepDialog) {
        LogSleepDialog(
            onDismiss = { showSleepDialog = false },
            onConfirm = { hours ->
                viewModel.logSleep(hours)
                showSleepDialog = false
            }
        )
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
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

@Composable
fun SleepCard(log: SleepLog, viewModel: HealthViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(log.date, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${log.hoursSlept}h sleep",
                    style = MaterialTheme.typography.bodyMedium, color = Color(0xFF818CF8))
            }
            IconButton(onClick = { viewModel.deleteSleep(log) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun HealthLogCard(log: HealthLog, viewModel: HealthViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(log.date, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MacroText("P", "${log.protein}g", Color(0xFF4ADE80))
                    MacroText("C", "${log.carbs}g", Color(0xFFF97316))
                    MacroText("F", "${log.fat}g", Color(0xFFFACC15))
                    MacroText("kcal", "${log.calories}", Color(0xFFA78BFA))
                }
            }
            IconButton(onClick = { viewModel.deleteHealth(log) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun MacroText(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, color = color)
    }
}

@Composable
fun LogHealthDialog(onDismiss: () -> Unit, onConfirm: (Int, Int, Int, Int, Float) -> Unit) {
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var water by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log nutrition") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = calories, onValueChange = { calories = it },
                    label = { Text("Calories (kcal)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = protein, onValueChange = { protein = it },
                    label = { Text("Protein (g)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = carbs, onValueChange = { carbs = it },
                    label = { Text("Carbs (g)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = fat, onValueChange = { fat = it },
                    label = { Text("Fat (g)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = water, onValueChange = { water = it },
                    label = { Text("Water (L)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    calories.toIntOrNull() ?: 0,
                    protein.toIntOrNull() ?: 0,
                    carbs.toIntOrNull() ?: 0,
                    fat.toIntOrNull() ?: 0,
                    water.toFloatOrNull() ?: 0f
                )
            }) { Text("Log", color = Color(0xFFA78BFA)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun LogSleepDialog(onDismiss: () -> Unit, onConfirm: (Float) -> Unit) {
    var hours by remember { mutableFloatStateOf(7f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log sleep") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Hours slept: ${String.format("%.1f", hours)}h",
                    style = MaterialTheme.typography.bodyMedium)
                Slider(
                    value = hours,
                    onValueChange = { hours = it },
                    valueRange = 0f..12f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF818CF8),
                        activeTrackColor = Color(0xFF818CF8)
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(hours) }) {
                Text("Log", color = Color(0xFFA78BFA))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}