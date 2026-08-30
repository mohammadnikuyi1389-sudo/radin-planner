package ir.parscode.app.domain.model

data class Habit(
    val id: Long = 0,
    val title: String,
    val iconKey: String,
    val targetLabel: String?,
    val createdDateIso: String,
)

/**
 * A habit combined with derived state for "today": whether it's checked
 * off, its current consecutive-day streak, and a rolling completion rate.
 * Computed in the repository from raw [HabitLogEntity] rows so screens
 * never do date math themselves.
 */
data class HabitWithProgress(
    val habit: Habit,
    val isDoneToday: Boolean,
    val streakDays: Int,
    val completionRatePercent: Int,
)
