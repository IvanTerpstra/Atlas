package com.example.atlas.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val current: Float = 0f,
    val target: Float = 100f,
    val isLongTerm: Boolean = false,
    val note: String = ""
)