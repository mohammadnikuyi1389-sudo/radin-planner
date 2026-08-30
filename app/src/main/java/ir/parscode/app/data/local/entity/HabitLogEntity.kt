package ir.parscode.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Presence of a row = habit was completed that day. Absence = not done.
 * Composite primary key on (habitId, dateIso) keeps "toggle today" a
 * single upsert/delete instead of a boolean flip on a wide row.
 */
@Entity(
    tableName = "habit_logs",
    primaryKeys = ["habitId", "dateIso"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("habitId"), Index("dateIso")],
)
data class HabitLogEntity(
    val habitId: Long,
    val dateIso: String,
)
