package ir.parscode.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.parscode.app.data.local.dao.*
import ir.parscode.app.data.local.entity.*

@Database(
    entities = [
        TaskEntity::class, HabitEntity::class, HabitLogEntity::class,
        GoalEntity::class, WeekEntity::class, PomodoroSessionEntity::class,
        LibraryItemEntity::class, FinanceRecordEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao
    abstract fun goalDao(): GoalDao
    abstract fun weekDao(): WeekDao
    abstract fun pomodoroDao(): PomodoroDao
    abstract fun libraryDao(): LibraryDao
    abstract fun financeDao(): FinanceDao

    companion object { const val DB_NAME = "parscode.db" }
}
