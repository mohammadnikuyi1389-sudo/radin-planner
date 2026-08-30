package ir.parscode.app.data.local.dao
import androidx.room.*
import ir.parscode.app.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY isCompleted ASC, id DESC")
    fun observeAll(): Flow<List<GoalEntity>>
    @Query("SELECT * FROM goals WHERE weekId = :weekId ORDER BY id ASC")
    fun observeByWeek(weekId: Long): Flow<List<GoalEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(g: GoalEntity): Long
    @Update suspend fun update(g: GoalEntity)
    @Delete suspend fun delete(g: GoalEntity)
}
