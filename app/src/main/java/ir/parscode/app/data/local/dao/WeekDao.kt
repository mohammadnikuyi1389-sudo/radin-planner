package ir.parscode.app.data.local.dao

import androidx.room.*
import ir.parscode.app.data.local.entity.WeekEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeekDao {
    @Query("SELECT * FROM weeks ORDER BY weekNumber ASC")
    fun getAll(): Flow<List<WeekEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(weeks: List<WeekEntity>)

    @Update
    suspend fun update(week: WeekEntity)
}
