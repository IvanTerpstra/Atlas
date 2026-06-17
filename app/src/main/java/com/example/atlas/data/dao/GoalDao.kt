package com.example.atlas.data.dao

import androidx.room.*
import com.example.atlas.data.models.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals") fun getAll(): Flow<List<Goal>>
    @Insert suspend fun insert(goal: Goal)
    @Update suspend fun update(goal: Goal)
    @Delete suspend fun delete(goal: Goal)
}