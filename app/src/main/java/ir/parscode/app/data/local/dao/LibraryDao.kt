package ir.parscode.app.data.local.dao

import androidx.room.*
import ir.parscode.app.data.local.entity.LibraryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Query("SELECT * FROM library_items ORDER BY sortOrder ASC")
    fun getAll(): Flow<List<LibraryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: LibraryItemEntity): Long

    @Update
    suspend fun update(item: LibraryItemEntity)

    @Delete
    suspend fun delete(item: LibraryItemEntity)
}
