package ir.parscode.app.data.local.dao

import androidx.room.*
import ir.parscode.app.data.local.entity.PomodoroSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroDao {
    @Query("SELECT * FROM pomodoro_sessions ORDER BY startMillis DESC")
    fun getAll(): Flow<List<PomodoroSessionEntity>>

    @Insert
    suspend fun insert(session: PomodoroSessionEntity): Long
}
