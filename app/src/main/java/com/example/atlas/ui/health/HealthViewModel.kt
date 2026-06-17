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

    private fun getTodayDate(): String = dateFormatter.format(Calendar.getInstance().time)

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

    fun logHealth(foodName: String, calories: Int, protein: Int, carbs: Int, fat: Int, waterMl: Int) {
        viewModelScope.launch {
            val today = getTodayDate()
            val existing = healthLogs.value.find { it.date == today }
            if (existing != null) {
                dao.updateHealth(
                    existing.copy(
                        foodName = if (foodName.isNotBlank()) foodName else existing.foodName,
                        calories = existing.calories + calories,
                        protein = existing.protein + protein,
                        carbs = existing.carbs + carbs,
                        fat = existing.fat + fat,
                        waterMl = existing.waterMl + waterMl
                    )
                )
            } else {
                dao.insertHealth(
                    HealthLog(
                        date = today,
                        foodName = foodName,
                        calories = calories,
                        protein = protein,
                        carbs = carbs,
                        fat = fat,
                        waterMl = waterMl
                    )
                )
            }
        }
    }

    fun addWater(ml: Int) {
        viewModelScope.launch {
            val today = getTodayDate()
            val existing = healthLogs.value.find { it.date == today }
            if (existing != null) {
                dao.updateHealth(existing.copy(waterMl = existing.waterMl + ml))
            } else {
                dao.insertHealth(HealthLog(date = today, waterMl = ml))
            }
        }
    }

    fun logSleep(hours: Float) {
        viewModelScope.launch {
            dao.insertSleep(
                SleepLog(
                    date = getTodayDate(),
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
    
    fun updateHealth(log: HealthLog, foodName: String, calories: Int, protein: Int, carbs: Int, fat: Int, waterMl: Int) {
        viewModelScope.launch {
            dao.updateHealth(
                log.copy(
                    foodName = foodName,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    waterMl = waterMl
                )
            )
        }
    }
}
