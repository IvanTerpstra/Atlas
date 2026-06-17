package com.example.atlas.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_plans")
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayOfWeek: String,
    val name: String,
    val estimatedMinutes: Int = 0,
    val isCompleted: Boolean = false,
    val startTime: String = ""
)