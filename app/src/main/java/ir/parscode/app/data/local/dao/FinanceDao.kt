package ir.parscode.app.data.local.dao
import androidx.room.*
import ir.parscode.app.data.local.entity.FinanceRecordEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface FinanceDao {
    @Query("SELECT * FROM finance_records ORDER BY dateIso DESC, id DESC")
    fun observeAll(): Flow<List<FinanceRecordEntity>>
    @Insert suspend fun insert(r: FinanceRecordEntity): Long
    @Update suspend fun update(r: FinanceRecordEntity)
    @Delete suspend fun delete(r: FinanceRecordEntity)
}
