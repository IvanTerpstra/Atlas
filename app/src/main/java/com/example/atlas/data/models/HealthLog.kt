package com.example.atlas.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_logs")
data class HealthLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val waterLiters: Float = 0f
)