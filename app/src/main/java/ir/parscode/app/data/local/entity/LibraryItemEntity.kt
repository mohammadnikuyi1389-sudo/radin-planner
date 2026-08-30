package ir.parscode.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "library_items")
data class LibraryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String = "BOOK",
    val url: String? = null,
    val note: String? = null,
    val sortOrder: Int = 0,
)
