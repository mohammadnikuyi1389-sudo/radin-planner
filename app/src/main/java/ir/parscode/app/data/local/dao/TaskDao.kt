package ir.parscode.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ir.parscode.app.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE dateIso = :dateIso ORDER BY sortOrder ASC, id ASC")
    fun observeForDate(dateIso: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE weekId = :weekId ORDER BY id ASC")
    fun observeForWeek(weekId: Long): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    // Keeps taskStatus in sync with isDone so the two never disagree (e.g. a
    // task ticked done here still shows correctly in the weekly-goal view).
    @Query("UPDATE tasks SET isDone = :isDone, taskStatus = CASE WHEN :isDone THEN 'DONE' ELSE 'TODO' END WHERE id = :taskId")
    suspend fun setDone(taskId: Long, isDone: Boolean)
}
