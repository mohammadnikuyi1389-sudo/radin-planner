package ir.parscode.app.data.local.dao

import androidx.room.*
import ir.parscode.app.data.local.entity.FinanceRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    @Query("SELECT * FROM finance_records ORDER BY dateMillis DESC")
    fun getAll(): Flow<List<FinanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: FinanceRecordEntity): Long

    @Delete
    suspend fun delete(record: FinanceRecordEntity)
}
