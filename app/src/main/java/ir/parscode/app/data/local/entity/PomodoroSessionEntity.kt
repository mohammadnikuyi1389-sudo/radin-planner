package ir.parscode.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateIso: String,
    val durationMinutes: Int,
    val kind: String, // FOCUS | SHORT_BREAK | LONG_BREAK
    val completedAtMillis: Long,
)
