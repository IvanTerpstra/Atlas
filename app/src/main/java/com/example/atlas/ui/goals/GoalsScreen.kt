package com.example.atlas.ui.goals

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
import com.example.atlas.data.models.Goal
import com.example.atlas.ui.ConfirmDeleteDialog

@Composable
fun GoalsScreen(viewModel: GoalViewModel = viewModel()) {
    val goals by viewModel.goals.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val shortTerm = goals.filter { !it.isLongTerm }
    val longTerm = goals.filter { it.isLongTerm }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFFA78BFA)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add goal", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (shortTerm.isNotEmpty()) {
                item {
                    SectionHeader("Short term", Color(0xFF22D3EE), shortTerm.size)
                }
                items(shortTerm) { goal ->
                    GoalCard(goal, viewModel)
                }
            }
            if (longTerm.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionHeader("Long term", Color(0xFFA78BFA), longTerm.size)
                }
                items(longTerm) { goal ->
                    GoalCard(goal, viewModel)
                }
            }
        }
    }

    if (showDialog) {
        AddGoalDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title, target, isLongTerm, note ->
                viewModel.addGoal(title, target, isLongTerm, note)
                showDialog = false
            }
        )
    }
}

@Composable
fun SectionHeader(label: String, color: Color, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.15f)) {
            Text("$count active", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}

@Composable
fun GoalCard(goal: Goal, viewModel: GoalViewModel) {
    var showConfirmDelete by remember { mutableStateOf(false) }

    if (showConfirmDelete) {
        ConfirmDeleteDialog(
            title = "Delete goal",
            message = "Delete \"${goal.title}\"?",
            onConfirm = {
                viewModel.deleteGoal(goal)
                showConfirmDelete = false
            },
            onDismiss = { showConfirmDelete = false }
        )
    }

    var showSlider by remember { mutableStateOf(false) }
    val progress = if (goal.target > 0) (goal.current / goal.target).coerceIn(0f, 1f) else 0f
    val pct = (progress * 100).toInt()
    val progressColor = when {
        pct >= 75 -> Color(0xFF4ADE80)
        pct >= 40 -> Color(0xFFA78BFA)
        else -> Color(0xFFF97316)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(goal.title, style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f))
                Text("$pct%", style = MaterialTheme.typography.labelMedium, color = progressColor)
                IconButton(onClick = { showConfirmDelete = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            if (goal.note.isNotBlank()) {
                Text(goal.note, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = { showSlider = !showSlider }) {
                Text("Update progress", color = Color(0xFFA78BFA),
                    style = MaterialTheme.typography.labelSmall)
            }
            if (showSlider) {
                var sliderValue by remember { mutableFloatStateOf(goal.current) }
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 0f..goal.target,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFA78BFA),
                        activeTrackColor = Color(0xFFA78BFA)),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = {
                        viewModel.updateProgress(goal, sliderValue)
                        showSlider = false
                    }) {
                        Text("Save", color = Color(0xFFA78BFA))
                    }
                }
            }
        }
    }
}

@Composable
fun AddGoalDialog(onDismiss: () -> Unit, onConfirm: (String, Float, Boolean, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("100") }
    var isLongTerm by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it },
                    label = { Text("Goal name") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = target, onValueChange = { target = it },
                    label = { Text("Target (e.g. 100)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = note, onValueChange = { note = it },
                    label = { Text("Note (optional)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isLongTerm, onCheckedChange = { isLongTerm = it })
                    Text("Long term goal", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val t = target.toFloatOrNull() ?: 100f
                if (title.isNotBlank()) onConfirm(title, t, isLongTerm, note)
            }) { Text("Add", color = Color(0xFFA78BFA)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}