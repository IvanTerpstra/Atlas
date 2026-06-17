package com.example.atlas.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 3,
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

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create new table with correct schema
                db.execSQL("""
            CREATE TABLE health_logs_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date TEXT NOT NULL,
                calories INTEGER NOT NULL DEFAULT 0,
                protein INTEGER NOT NULL DEFAULT 0,
                carbs INTEGER NOT NULL DEFAULT 0,
                fat INTEGER NOT NULL DEFAULT 0,
                waterMl INTEGER NOT NULL DEFAULT 0,
                foodName TEXT NOT NULL DEFAULT ''
            )
        """.trimIndent())

                // Copy existing data, converting waterLiters to waterMl
                db.execSQL("""
            INSERT INTO health_logs_new (id, date, calories, protein, carbs, fat, waterMl, foodName)
            SELECT id, date, calories, protein, carbs, fat, CAST(waterLiters * 1000 AS INTEGER), ''
            FROM health_logs
        """.trimIndent())

                // Drop old table and rename new one
                db.execSQL("DROP TABLE health_logs")
                db.execSQL("ALTER TABLE health_logs_new RENAME TO health_logs")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE workout_plans ADD COLUMN startTime TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "atlas_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build().also { INSTANCE = it }
            }
        }
    }
}