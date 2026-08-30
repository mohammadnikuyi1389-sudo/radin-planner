package ir.parscode.app.domain.repository

import ir.parscode.app.domain.model.Task
import ir.parscode.app.domain.model.TaskPriority
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasksForDate(dateIso: String): Flow<List<Task>>

    suspend fun addTask(
        dateIso: String,
        title: String,
        timeLabel: String?,
        durationMinutes: Int?,
        priority: TaskPriority,
        category: String = "شخصی",
    )

    suspend fun setDone(taskId: Long, isDone: Boolean)
    suspend fun updateTask(task: Task)
    suspend fun delete(task: Task)
}
