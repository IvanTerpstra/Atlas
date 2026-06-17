package com.example.atlas.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skills")
data class Skill(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String,
    val currentLevel: Int = 0,
    val targetLevel: Int = 10
)