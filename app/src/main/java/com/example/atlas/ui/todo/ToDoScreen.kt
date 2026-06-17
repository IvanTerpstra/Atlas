package com.example.atlas.ui.todo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atlas.data.models.Task
import com.example.atlas.ui.ConfirmDeleteDialog

@Composable
fun TodoScreen(viewModel: TaskViewModel = viewModel()) {
    val tasks by viewModel.tasks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val categoryColors = mapOf(
        "Fitness" to Color(0xFF4ADE80),
        "School" to Color(0xFF818CF8),
        "Health" to Color(0xFFF97316),
        "Personal" to Color(0xFF22D3EE),
        "Career" to Color(0xFFFACC15)
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFFA78BFA)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add task", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val today = tasks.filter { !it.isUpcoming }
            val upcoming = tasks.filter { it.isUpcoming }

            if (today.isNotEmpty()) {
                item {
                    Text("Today", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(today) { task ->
                    TaskCard(task, categoryColors, viewModel)
                }
            }

            if (upcoming.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Upcoming", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(upcoming) { task ->
                    TaskCard(task, categoryColors, viewModel)
                }
            }
        }
    }

    if (showDialog) {
        AddTaskDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title, category, isUpcoming ->
                viewModel.addTask(title, category, isUpcoming)
                showDialog = false
            }
        )
    }
}

@Composable
fun TaskCard(task: Task, categoryColors: Map<String, Color>, viewModel: TaskViewModel) {
    var showConfirmDelete by remember { mutableStateOf(false) }

    if (showConfirmDelete) {
        ConfirmDeleteDialog(
            title = "Delete task",
            message = "Delete \"${task.title}\"?",
            onConfirm = {
                viewModel.deleteTask(task)
                showConfirmDelete = false
            },
            onDismiss = { showConfirmDelete = false }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { viewModel.toggleTask(task) },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFA78BFA))
            )
            Text(
                text = task.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface
            )
            val color = categoryColors[task.category] ?: Color(0xFF888780)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = color.copy(alpha = 0.15f)
            ) {
                Text(
                    text = task.category,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }
            IconButton(onClick = { showConfirmDelete = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onConfirm: (String, String, Boolean) -> Unit) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Personal") }
    var isUpcoming by remember { mutableStateOf(false) }
    val categories = listOf("Fitness", "School", "Health", "Personal", "Career")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Category", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isUpcoming, onCheckedChange = { isUpcoming = it })
                    Text("Upcoming task", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { if (title.isNotBlank()) onConfirm(title, category, isUpcoming) }) {
                Text("Add", color = Color(0xFFA78BFA))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}