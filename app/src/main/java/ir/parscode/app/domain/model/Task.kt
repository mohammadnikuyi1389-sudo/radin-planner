package ir.parscode.app.domain.model

enum class TaskPriority { HIGH, MEDIUM, LOW }

data class Task(
    val id: Long = 0,
    val dateIso: String,
    val title: String,
    val timeLabel: String?,
    val durationMinutes: Int?,
    val priority: TaskPriority,
    val category: String,
    val isDone: Boolean,
    val sortOrder: Int,
    val weekId: Long? = null,
    val goalId: Long? = null,
    val taskStatus: String = "TODO",
)
