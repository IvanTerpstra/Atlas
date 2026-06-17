package com.example.atlas.ui.skillz

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
import com.example.atlas.data.models.Skill

@Composable
fun SkillzScreen(viewModel: SkillViewModel = viewModel()) {
    val skills by viewModel.skills.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val categoryColors = mapOf(
        "Tech" to Color(0xFF818CF8),
        "Fitness" to Color(0xFF4ADE80),
        "Creative" to Color(0xFFF97316),
        "Academic" to Color(0xFF22D3EE),
        "Other" to Color(0xFFFACC15)
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFFA78BFA)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add skill", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Skillz", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
            }
            items(skills) { skill ->
                SkillCard(skill, categoryColors, viewModel)
            }
        }
    }

    if (showDialog) {
        AddSkillDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title, category, targetLevel ->
                viewModel.addSkill(title, category, targetLevel)
                showDialog = false
            }
        )
    }
}

@Composable
fun SkillCard(skill: Skill, categoryColors: Map<String, Color>, viewModel: SkillViewModel) {
    var showSlider by remember { mutableStateOf(false) }
    val progress = if (skill.targetLevel > 0)
        (skill.currentLevel.toFloat() / skill.targetLevel.toFloat()).coerceIn(0f, 1f) else 0f
    val pct = (progress * 100).toInt()
    val progressColor = when {
        pct >= 75 -> Color(0xFF4ADE80)
        pct >= 40 -> Color(0xFFA78BFA)
        else -> Color(0xFFF97316)
    }
    val categoryColor = categoryColors[skill.category] ?: Color(0xFF888780)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(skill.title, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(shape = RoundedCornerShape(20.dp), color = categoryColor.copy(alpha = 0.15f)) {
                        Text(skill.category,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall, color = categoryColor)
                    }
                }
                Text("${skill.currentLevel}/${skill.targetLevel}",
                    style = MaterialTheme.typography.labelMedium, color = progressColor)
                IconButton(onClick = { viewModel.deleteSkill(skill) }) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Level $pct%", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = { showSlider = !showSlider }) {
                    Text("Update level", color = Color(0xFFA78BFA),
                        style = MaterialTheme.typography.labelSmall)
                }
            }
            if (showSlider) {
                var sliderValue by remember { mutableIntStateOf(skill.currentLevel) }
                Slider(
                    value = sliderValue.toFloat(),
                    onValueChange = { sliderValue = it.toInt() },
                    valueRange = 0f..skill.targetLevel.toFloat(),
                    steps = skill.targetLevel - 1,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFA78BFA),
                        activeTrackColor = Color(0xFFA78BFA)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Level $sliderValue", style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA78BFA))
                    TextButton(onClick = {
                        viewModel.updateLevel(skill, sliderValue)
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
fun AddSkillDialog(onDismiss: () -> Unit, onConfirm: (String, String, Int) -> Unit) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Tech") }
    var targetLevel by remember { mutableIntStateOf(10) }
    val categories = listOf("Tech", "Fitness", "Creative", "Academic", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New skill") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Skill name") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Category", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                Text("Target level: $targetLevel", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Slider(
                    value = targetLevel.toFloat(),
                    onValueChange = { targetLevel = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFA78BFA),
                        activeTrackColor = Color(0xFFA78BFA)
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotBlank()) onConfirm(title, category, targetLevel)
            }) { Text("Add", color = Color(0xFFA78BFA)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}