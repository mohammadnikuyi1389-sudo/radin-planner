package ir.parscode.app.data.local.dao
import androidx.room.*
import ir.parscode.app.data.local.entity.WeekEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface WeekDao {
    @Query("SELECT * FROM program_weeks ORDER BY weekNumber ASC")
    fun observeAll(): Flow<List<WeekEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(w: WeekEntity): Long
    @Query("SELECT COUNT(*) FROM program_weeks") suspend fun count(): Int
}
