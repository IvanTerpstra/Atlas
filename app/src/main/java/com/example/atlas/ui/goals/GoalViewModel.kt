package com.example.atlas.ui.goals

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlas.data.db.AppDatabase
import com.example.atlas.data.models.Goal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getDatabase(app).goalDao()

    val goals = dao.getAll().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addGoal(title: String, target: Float, isLongTerm: Boolean, note: String = "") {
        viewModelScope.launch {
            dao.insert(Goal(title = title, target = target, isLongTerm = isLongTerm, note = note))
        }
    }

    fun updateProgress(goal: Goal, newCurrent: Float) {
        viewModelScope.launch {
            dao.update(goal.copy(current = newCurrent))
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            dao.delete(goal)
        }
    }
}