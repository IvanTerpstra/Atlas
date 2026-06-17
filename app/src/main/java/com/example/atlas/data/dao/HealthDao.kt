package com.example.atlas.data.dao

import androidx.room.*
import com.example.atlas.data.models.HealthLog
import com.example.atlas.data.models.SleepLog
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {
    @Query("SELECT * FROM health_logs") fun getAllHealth(): Flow<List<HealthLog>>
    @Insert suspend fun insertHealth(log: HealthLog)
    @Update suspend fun updateHealth(log: HealthLog)
    @Delete suspend fun deleteHealth(log: HealthLog)

    @Query("SELECT * FROM sleep_logs") fun getAllSleep(): Flow<List<SleepLog>>
    @Insert suspend fun insertSleep(log: SleepLog)
    @Delete suspend fun deleteSleep(log: SleepLog)
}