package ir.parscode.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    val priority: String = "MEDIUM",
    val deadlineIso: String? = null,
    val progressPercent: Int = 0,
    val isCompleted: Boolean = false,
    val createdDateIso: String,
    val weekId: Long? = null, // links this goal to a 12-week program week, if any
)
