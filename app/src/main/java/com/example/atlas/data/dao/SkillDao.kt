package com.example.atlas.data.dao

import androidx.room.*
import com.example.atlas.data.models.Skill
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {
    @Query("SELECT * FROM skills") fun getAll(): Flow<List<Skill>>
    @Insert suspend fun insert(skill: Skill)
    @Update suspend fun update(skill: Skill)
    @Delete suspend fun delete(skill: Skill)
}