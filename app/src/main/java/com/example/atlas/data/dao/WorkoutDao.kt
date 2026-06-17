package com.example.atlas.data.dao

import androidx.room.*
import com.example.atlas.data.models.Exercise
import com.example.atlas.data.models.WorkoutPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_plans") fun getAllPlans(): Flow<List<WorkoutPlan>>
    @Insert suspend fun insertPlan(plan: WorkoutPlan)
    @Update suspend fun updatePlan(plan: WorkoutPlan)
    @Delete suspend fun deletePlan(plan: WorkoutPlan)

    @Query("SELECT * FROM exercises WHERE workoutPlanId = :planId") fun getExercises(planId: Int): Flow<List<Exercise>>
    @Insert suspend fun insertExercise(exercise: Exercise)
    @Delete suspend fun deleteExercise(exercise: Exercise)
}