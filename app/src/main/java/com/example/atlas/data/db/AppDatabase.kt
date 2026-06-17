package com.example.atlas.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.atlas.data.dao.*
import com.example.atlas.data.models.*

@Database(
    entities = [
        Task::class,
        Goal::class,
        HealthLog::class,
        SleepLog::class,
        WorkoutPlan::class,
        Exercise::class,
        Skill::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun goalDao(): GoalDao
    abstract fun healthDao(): HealthDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun skillDao(): SkillDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "atlas_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}