package com.example.atlas.ui.todo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlas.data.db.AppDatabase
import com.example.atlas.data.models.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getDatabase(app).taskDao()

    val tasks = dao.getAll().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addTask(title: String, category: String, isUpcoming: Boolean = false) {
        viewModelScope.launch {
            dao.insert(Task(title = title, category = category, isUpcoming = isUpcoming))
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            dao.update(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.delete(task)
        }
    }
}