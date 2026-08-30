package ir.parscode.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "finance_records")
data class FinanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // INCOME | EXPENSE
    val amount: Long,
    val category: String,
    val note: String? = null,
    val dateIso: String,
)
