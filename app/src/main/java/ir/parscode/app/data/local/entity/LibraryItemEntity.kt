package ir.parscode.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "library_items")
data class LibraryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val author: String? = null,
    val category: String = "آموزشی",
    val progressPercent: Int = 0,
    val isFavorite: Boolean = false,
    val lastViewedIso: String? = null,
    val sortOrder: Int = 0,
)
