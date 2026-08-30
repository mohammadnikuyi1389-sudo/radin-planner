package ir.parscode.app.domain.repository

import ir.parscode.app.domain.model.HabitWithProgress
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    /** Active habits joined with today's completion + streak, kept live. */
    fun observeHabitsForDate(dateIso: String): Flow<List<HabitWithProgress>>

    suspend fun addHabit(title: String, iconKey: String, targetLabel: String?)
    suspend fun toggleDone(habitId: Long, dateIso: String, isDone: Boolean)
    suspend fun updateHabit(habitId: Long, title: String, targetLabel: String?)
    suspend fun archive(habitId: Long)
}
