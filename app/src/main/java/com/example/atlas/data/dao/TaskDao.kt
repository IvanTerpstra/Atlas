package com.example.atlas.data.dao

import androidx.room.*
import com.example.atlas.data.models.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks") fun getAll(): Flow<List<Task>>
    @Insert suspend fun insert(task: Task)
    @Update suspend fun update(task: Task)
    @Delete suspend fun delete(task: Task)
}