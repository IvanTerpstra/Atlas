package com.example.atlas.ui.health

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atlas.data.db.AppDatabase
import com.example.atlas.data.models.HealthLog
import com.example.atlas.data.models.SleepLog
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HealthViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getDatabase(app).healthDao()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val healthLogs = dao.getAllHealth().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val sleepLogs = dao.getAllSleep().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun logHealth(calories: Int, protein: Int, carbs: Int, fat: Int, waterLiters: Float) {
        viewModelScope.launch {
            dao.insertHealth(
                HealthLog(
                    date = dateFormatter.format(Calendar.getInstance().time),
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    waterLiters = waterLiters
                )
            )
        }
    }

    fun logSleep(hours: Float) {
        viewModelScope.launch {
            dao.insertSleep(
                SleepLog(
                    date = dateFormatter.format(Calendar.getInstance().time),
                    hoursSlept = hours
                )
            )
        }
    }

    fun deleteHealth(log: HealthLog) {
        viewModelScope.launch { dao.deleteHealth(log) }
    }

    fun deleteSleep(log: SleepLog) {
        viewModelScope.launch { dao.deleteSleep(log) }
    }
}