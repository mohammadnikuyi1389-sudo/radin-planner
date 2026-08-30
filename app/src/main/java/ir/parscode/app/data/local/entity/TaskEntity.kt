package ir.parscode.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single to-do item for a given calendar day (used by both the Dashboard
 * "وظایف امروز" card and the full برنامه روزانه screen).
 *
 * [dateIso] is stored as "YYYY-MM-DD" (Gregorian, used only as a stable sort
 * key) so querying "today's tasks" is a plain equality match - Jalali
 * conversion for display happens in the UI layer via DateUtils, mirroring
 * how the previous web app kept storage keys Gregorian and did display-only
 * conversion at render time.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateIso: String,
    val title: String,
    val timeLabel: String? = null,   // e.g. "08:00", optional
    val durationMinutes: Int? = null,
    val priority: String = "MEDIUM", // HIGH | MEDIUM | LOW
    val category: String = "شخصی",   // درس | سلامت | شخصی | کاری | پروژه
    val isDone: Boolean = false,
    val sortOrder: Int = 0,
    val weekId: Long? = null,         // links this task to a 12-week program week, if any
    val goalId: Long? = null,         // links this task to a specific weekly goal, if any
    val taskStatus: String = "TODO",  // TODO | PENDING | DONE - tri-state for weekly-goal tracking
)
