package ir.parscode.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A habit definition. Daily completion is tracked separately in
 * [HabitLogEntity] (one row per habit per day it was marked done) so
 * streaks and completion history are queryable without rewriting this row.
 */
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val iconKey: String = "target",       // maps to a Material icon in the UI layer
    val goalMinutesPerDay: Int? = null,
    val targetPerDay: String? = null,     // free-text target label, e.g. "۸ لیوان"
    val createdDateIso: String,
    val archivedDateIso: String? = null,  // null = active habit
    val sortOrder: Int = 0,
)
