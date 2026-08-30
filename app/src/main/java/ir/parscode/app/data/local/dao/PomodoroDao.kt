package ir.parscode.app.data.local.dao
import androidx.room.*
import ir.parscode.app.data.local.entity.PomodoroSessionEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface PomodoroDao {
    @Query("SELECT * FROM pomodoro_sessions WHERE dateIso = :dateIso ORDER BY completedAtMillis DESC")
    fun observeForDate(dateIso: String): Flow<List<PomodoroSessionEntity>>
    @Insert suspend fun insert(s: PomodoroSessionEntity): Long
}
