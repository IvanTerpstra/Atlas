package com.example.atlas.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutPlanId: Int,
    val name: String,
    val sets: Int = 0,
    val reps: Int = 0
)