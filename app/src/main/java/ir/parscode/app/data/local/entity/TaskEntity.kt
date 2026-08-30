package ir.parscode.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateIso: String,
    val title: String,
    val timeLabel: String? = null,
    val durationMinutes: Int? = null,
    val priority: String = "MEDIUM",
    val category: String = "شخصی",
    val isDone: Boolean = false,
    val sortOrder: Int = 0,
    val weekId: Long? = null,
    val goalId: Long? = null,
    val taskStatus: String = "TODO",
    val isSample: Boolean = false,
)
