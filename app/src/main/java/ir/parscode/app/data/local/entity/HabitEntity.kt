package ir.parscode.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val iconKey: String = "target",
    val goalMinutesPerDay: Int? = null,
    val targetPerDay: String? = null,
    val createdDateIso: String,
    val archivedDateIso: String? = null,
    val sortOrder: Int = 0,
)
