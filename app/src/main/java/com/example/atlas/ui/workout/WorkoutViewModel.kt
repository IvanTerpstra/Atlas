package com.example.atlas.ui.workout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlas.data.db.AppDatabase
import com.example.atlas.data.models.Exercise
import com.example.atlas.data.models.WorkoutPlan
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getDatabase(app).workoutDao()

    val workoutPlans = dao.getAllPlans().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun getExercises(planId: Int) = dao.getExercises(planId).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addPlan(dayOfWeek: String, name: String, estimatedMinutes: Int, startTime: String = "") {
        viewModelScope.launch {
            dao.insertPlan(WorkoutPlan(
                dayOfWeek = dayOfWeek,
                name = name,
                estimatedMinutes = estimatedMinutes,
                startTime = startTime
            ))
        }
    }

    fun togglePlanComplete(plan: WorkoutPlan) {
        viewModelScope.launch {
            dao.updatePlan(plan.copy(isCompleted = !plan.isCompleted))
        }
    }

    fun deletePlan(plan: WorkoutPlan) {
        viewModelScope.launch { dao.deletePlan(plan) }
    }

    fun addExercise(planId: Int, name: String, sets: Int, reps: Int) {
        viewModelScope.launch {
            dao.insertExercise(Exercise(
                workoutPlanId = planId,
                name = name,
                sets = sets,
                reps = reps
            ))
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch { dao.deleteExercise(exercise) }
    }
}