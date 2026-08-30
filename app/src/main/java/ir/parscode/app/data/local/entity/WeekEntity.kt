package ir.parscode.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "program_weeks")
data class WeekEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weekNumber: Int,
    val title: String,
    val description: String? = null,
    val progressPercent: Int = 0,
    val status: String = "UPCOMING",
    val isSample: Boolean = false,
)
