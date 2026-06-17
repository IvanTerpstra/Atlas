package com.example.atlas.ui.skillz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlas.data.db.AppDatabase
import com.example.atlas.data.models.Skill
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SkillViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getDatabase(app).skillDao()

    val skills = dao.getAll().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addSkill(title: String, category: String, targetLevel: Int = 10) {
        viewModelScope.launch {
            dao.insert(Skill(title = title, category = category, targetLevel = targetLevel))
        }
    }

    fun updateLevel(skill: Skill, newLevel: Int) {
        viewModelScope.launch {
            dao.update(skill.copy(currentLevel = newLevel))
        }
    }

    fun deleteSkill(skill: Skill) {
        viewModelScope.launch {
            dao.delete(skill)
        }
    }
}