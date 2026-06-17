package com.example.atlas.ui.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atlas.data.models.HealthLog
import com.example.atlas.data.models.SleepLog
import com.example.atlas.ui.ConfirmDeleteDialog
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HealthScreen(viewModel: HealthViewModel = viewModel()) {
    val healthLogs by viewModel.healthLogs.collectAsState()
    val sleepLogs by viewModel.sleepLogs.collectAsState()
    var showHealthDialog by remember { mutableStateOf(false) }
    var showSleepDialog by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    val todayHealth = healthLogs.find { it.date == today }
    val avgSleep = if (sleepLogs.isNotEmpty())
        sleepLogs.takeLast(7).map { it.hoursSlept }.average().toFloat() else 0f

    val waterDisplay = if ((todayHealth?.waterMl ?: 0) >= 1000)
        "${"%.1f".format((todayHealth?.waterMl ?: 0) / 1000f)}L"
    else "${todayHealth?.waterMl ?: 0}ml"

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
                    StatCard("Water", waterDisplay,
                        Color(0xFF22D3EE), Modifier.weight(1f))
                }
            }
            item {
                WaterQuickAdd(viewModel)
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
            onConfirm = { foodName, calories, protein, carbs, fat, waterMl ->
                viewModel.logHealth(foodName, calories, protein, carbs, fat, waterMl)
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
fun WaterQuickAdd(viewModel: HealthViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Quick add water", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { viewModel.addWater(250) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("☕", style = MaterialTheme.typography.bodySmall)
                        Text("Cup", style = MaterialTheme.typography.labelSmall)
                        Text("250ml", style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF22D3EE))
                    }
                }
                OutlinedButton(
                    onClick = { viewModel.addWater(650) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🥤", style = MaterialTheme.typography.bodySmall)
                        Text("Glass", style = MaterialTheme.typography.labelSmall)
                        Text("650ml", style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF22D3EE))
                    }
                }
                OutlinedButton(
                    onClick = { viewModel.addWater(1000) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🍶", style = MaterialTheme.typography.bodySmall)
                        Text("Bottle", style = MaterialTheme.typography.labelSmall)
                        Text("1000ml", style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF22D3EE))
                    }
                }
            }
        }
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
    var showConfirmDelete by remember { mutableStateOf(false) }

    if (showConfirmDelete) {
        ConfirmDeleteDialog(
            title = "Delete sleep log",
            message = "Delete sleep log for ${log.date}?",
            onConfirm = {
                viewModel.deleteSleep(log)
                showConfirmDelete = false
            },
            onDismiss = { showConfirmDelete = false }
        )
    }


    val hours = log.hoursSlept.toInt()
    val minutes = ((log.hoursSlept - hours) * 60).toInt()
    val displayTime = "${hours}h ${minutes}m"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(log.date, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$displayTime sleep",
                    style = MaterialTheme.typography.bodyMedium, color = Color(0xFF818CF8))
            }
            IconButton(onClick = { showConfirmDelete = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun HealthLogCard(log: HealthLog, viewModel: HealthViewModel) {
    var showConfirmDelete by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    if (showConfirmDelete) {
        ConfirmDeleteDialog(
            title = "Delete nutrition log",
            message = "Delete log for ${log.date}?",
            onConfirm = {
                viewModel.deleteHealth(log)
                showConfirmDelete = false
            },
            onDismiss = { showConfirmDelete = false }
        )
    }

    if (showEditDialog) {
        EditHealthDialog(
            log = log,
            onDismiss = { showEditDialog = false },
            onConfirm = { foodName, calories, protein, carbs, fat, waterMl ->
                viewModel.updateHealth(log, foodName, calories, protein, carbs, fat, waterMl)
                showEditDialog = false
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(log.date, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (log.foodName.isNotBlank()) {
                        Text(log.foodName, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { showConfirmDelete = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MacroText("P", "${log.protein}g", Color(0xFF4ADE80))
                MacroText("C", "${log.carbs}g", Color(0xFFF97316))
                MacroText("F", "${log.fat}g", Color(0xFFFACC15))
                MacroText("kcal", "${log.calories}", Color(0xFFA78BFA))
                MacroText("💧", if (log.waterMl >= 1000) "${"%.1f".format(log.waterMl / 1000f)}L"
                else "${log.waterMl}ml", Color(0xFF22D3EE))
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
fun LogHealthDialog(onDismiss: () -> Unit, onConfirm: (String, Int, Int, Int, Int, Int) -> Unit) {
    var foodName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var waterMl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log nutrition") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = foodName, onValueChange = { foodName = it },
                    label = { Text("What did you eat?") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
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
                OutlinedTextField(value = waterMl, onValueChange = { waterMl = it },
                    label = { Text("Water (ml)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    foodName,
                    calories.toIntOrNull() ?: 0,
                    protein.toIntOrNull() ?: 0,
                    carbs.toIntOrNull() ?: 0,
                    fat.toIntOrNull() ?: 0,
                    waterMl.toIntOrNull() ?: 0
                )
            }) { Text("Log", color = Color(0xFFA78BFA)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditHealthDialog(log: HealthLog, onDismiss: () -> Unit, onConfirm: (String, Int, Int, Int, Int, Int) -> Unit) {
    var foodName by remember { mutableStateOf(log.foodName) }
    var calories by remember { mutableStateOf(log.calories.toString()) }
    var protein by remember { mutableStateOf(log.protein.toString()) }
    var carbs by remember { mutableStateOf(log.carbs.toString()) }
    var fat by remember { mutableStateOf(log.fat.toString()) }
    var waterMl by remember { mutableStateOf(log.waterMl.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit log") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = foodName, onValueChange = { foodName = it },
                    label = { Text("What did you eat?") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
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
                OutlinedTextField(value = waterMl, onValueChange = { waterMl = it },
                    label = { Text("Water (ml)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    foodName,
                    calories.toIntOrNull() ?: 0,
                    protein.toIntOrNull() ?: 0,
                    carbs.toIntOrNull() ?: 0,
                    fat.toIntOrNull() ?: 0,
                    waterMl.toIntOrNull() ?: 0
                )
            }) { Text("Save", color = Color(0xFFA78BFA)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun LogSleepDialog(onDismiss: () -> Unit, onConfirm: (Float) -> Unit) {
    var bedHour by remember { mutableIntStateOf(22) }
    var bedMinute by remember { mutableIntStateOf(0) }
    var wakeHour by remember { mutableIntStateOf(7) }
    var wakeMinute by remember { mutableIntStateOf(0) }

    val totalMinutes = run {
        val bedTotal = bedHour * 60 + bedMinute
        val wakeTotal = wakeHour * 60 + wakeMinute
        if (wakeTotal >= bedTotal) wakeTotal - bedTotal
        else (24 * 60 - bedTotal) + wakeTotal
    }
    val hoursSlept = totalMinutes / 60
    val minutesSlept = totalMinutes % 60

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log sleep") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Bedtime", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = bedHour.toString().padStart(2, '0'),
                        onValueChange = { bedHour = it.toIntOrNull()?.coerceIn(0, 23) ?: bedHour },
                        label = { Text("HH") }, singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Text(":", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = bedMinute.toString().padStart(2, '0'),
                        onValueChange = { bedMinute = it.toIntOrNull()?.coerceIn(0, 59) ?: bedMinute },
                        label = { Text("MM") }, singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text("Wake time", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = wakeHour.toString().padStart(2, '0'),
                        onValueChange = { wakeHour = it.toIntOrNull()?.coerceIn(0, 23) ?: wakeHour },
                        label = { Text("HH") }, singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Text(":", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = wakeMinute.toString().padStart(2, '0'),
                        onValueChange = { wakeMinute = it.toIntOrNull()?.coerceIn(0, 59) ?: wakeMinute },
                        label = { Text("MM") }, singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF818CF8).copy(alpha = 0.15f))
                ) {
                    Text(
                        "Slept: ${hoursSlept}h ${minutesSlept}m",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF818CF8)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(totalMinutes / 60f) }) {
                Text("Log", color = Color(0xFFA78BFA))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}