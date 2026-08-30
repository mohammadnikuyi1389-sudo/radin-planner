package ir.parscode.app.data.repository

import ir.parscode.app.data.local.dao.HabitDao
import ir.parscode.app.data.local.entity.HabitEntity
import ir.parscode.app.data.local.entity.HabitLogEntity
import ir.parscode.app.domain.model.Habit
import ir.parscode.app.domain.model.HabitWithProgress
import ir.parscode.app.domain.repository.HabitRepository
import ir.parscode.app.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.temporal.ChronoUnit

class HabitRepositoryImpl(private val dao: HabitDao) : HabitRepository {

    override fun observeHabitsForDate(dateIso: String): Flow<List<HabitWithProgress>> =
        combine(dao.observeActive(), dao.observeLogsForDate(dateIso), dao.observeAllLogs()) { habits, logsToday, allLogs ->
            val doneTodayIds = logsToday.map { it.habitId }.toSet()
            val logsByHabit = allLogs.groupBy { it.habitId }
            habits.map { entity ->
                val loggedDates = logsByHabit[entity.id]?.map { it.dateIso }?.toSet() ?: emptySet()
                HabitWithProgress(
                    habit = entity.toDomain(),
                    isDoneToday = entity.id in doneTodayIds,
                    streakDays = computeStreak(loggedDates, dateIso),
                    completionRatePercent = computeCompletionRate(loggedDates, entity.createdDateIso, dateIso),
                )
            }
        }

    override suspend fun addHabit(title: String, iconKey: String, targetLabel: String?) {
        dao.upsert(
            HabitEntity(
                title = title,
                iconKey = iconKey,
                targetPerDay = targetLabel,
                createdDateIso = DateUtils.todayIso(),
            )
        )
    }

    override suspend fun toggleDone(habitId: Long, dateIso: String, isDone: Boolean) {
        if (isDone) {
            dao.logDone(HabitLogEntity(habitId, dateIso))
        } else {
            dao.unlog(habitId, dateIso)
        }
    }

    override suspend fun archive(habitId: Long) {
        dao.archive(habitId, DateUtils.todayIso())
    }

    override suspend fun updateHabit(habitId: Long, title: String, targetLabel: String?) {
        val existing = dao.getById(habitId) ?: return
        dao.update(existing.copy(title = title, targetPerDay = targetLabel))
    }
}

/**
 * Consecutive days ending at [todayIso] (or ending yesterday if today isn't
 * logged yet, so a streak doesn't visually reset to 0 before the day is
 * even over).
 */
private fun computeStreak(loggedDates: Set<String>, todayIso: String): Int {
    var cursor = if (todayIso in loggedDates) todayIso else DateUtils.addDaysIso(todayIso, -1)
    var streak = 0
    while (cursor in loggedDates) {
        streak++
        cursor = DateUtils.addDaysIso(cursor, -1)
    }
    return streak
}

private fun computeCompletionRate(loggedDates: Set<String>, createdDateIso: String, todayIso: String): Int {
    val created = DateUtils.parseIso(createdDateIso)
    val today = DateUtils.parseIso(todayIso)
    val totalDays = (ChronoUnit.DAYS.between(created, today) + 1).coerceAtLeast(1)
    val rate = (loggedDates.size * 100.0 / totalDays)
    return rate.coerceIn(0.0, 100.0).toInt()
}

private fun HabitEntity.toDomain() = Habit(
    id = id,
    title = title,
    iconKey = iconKey,
    targetLabel = targetPerDay,
    createdDateIso = createdDateIso,
)
