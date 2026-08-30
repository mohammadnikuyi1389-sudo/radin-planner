package ir.parscode.app.data.repository

import ir.parscode.app.data.local.dao.TaskDao
import ir.parscode.app.data.local.entity.TaskEntity
import ir.parscode.app.domain.model.Task
import ir.parscode.app.domain.model.TaskPriority
import ir.parscode.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(private val dao: TaskDao) : TaskRepository {

    override fun observeTasksForDate(dateIso: String): Flow<List<Task>> =
        dao.observeForDate(dateIso).map { list -> list.map { it.toDomain() } }

    override suspend fun addTask(
        dateIso: String,
        title: String,
        timeLabel: String?,
        durationMinutes: Int?,
        priority: TaskPriority,
        category: String,
    ) {
        dao.upsert(
            TaskEntity(
                dateIso = dateIso,
                title = title,
                timeLabel = timeLabel,
                durationMinutes = durationMinutes,
                priority = priority.name,
                category = category,
            )
        )
    }

    override suspend fun setDone(taskId: Long, isDone: Boolean) {
        dao.setDone(taskId, isDone)
    }

    override suspend fun updateTask(task: Task) {
        dao.update(task.toEntity())
    }

    override suspend fun delete(task: Task) {
        dao.delete(task.toEntity())
    }
}

private fun TaskEntity.toDomain() = Task(
    id = id,
    dateIso = dateIso,
    title = title,
    timeLabel = timeLabel,
    durationMinutes = durationMinutes,
    priority = runCatching { TaskPriority.valueOf(priority) }.getOrDefault(TaskPriority.MEDIUM),
    category = category,
    isDone = isDone,
    sortOrder = sortOrder,
    weekId = weekId,
    goalId = goalId,
    taskStatus = taskStatus,
)

private fun Task.toEntity() = TaskEntity(
    id = id,
    dateIso = dateIso,
    title = title,
    timeLabel = timeLabel,
    durationMinutes = durationMinutes,
    priority = priority.name,
    category = category,
    isDone = isDone,
    sortOrder = sortOrder,
    weekId = weekId,
    goalId = goalId,
    taskStatus = taskStatus,
)
