package ir.parscode.app.data.local.dao
import androidx.room.*
import ir.parscode.app.data.local.entity.LibraryItemEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface LibraryDao {
    @Query("SELECT * FROM library_items ORDER BY id DESC")
    fun observeAll(): Flow<List<LibraryItemEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(i: LibraryItemEntity): Long
    @Delete suspend fun delete(i: LibraryItemEntity)
    @Delete suspend fun deleteAll(items: List<LibraryItemEntity>)
    @Update suspend fun updateAll(items: List<LibraryItemEntity>)
}
