package ir.parscode.app.ui.screens.dailyplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ir.parscode.app.domain.model.Task
import ir.parscode.app.domain.model.TaskPriority
import ir.parscode.app.domain.repository.TaskRepository
import ir.parscode.app.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val DAY_START_LABEL = "۰۶:۳۰"
const val DAY_END_LABEL = "۲۲:۳۰"
private const val DAILY_BUDGET_MINUTES = 600

data class DailyPlanUiState(
    val dateIso: String = DateUtils.todayIso(),
    val tasks: List<Task> = emptyList(),
    val doneCount: Int = 0,
    val totalCount: Int = 0,
    val progressPercent: Int = 0,
    val plannedMinutes: Int = 0,
    val budgetMinutes: Int = DAILY_BUDGET_MINUTES,
)

class DailyPlanViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _dateIso = MutableStateFlow(DateUtils.todayIso())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DailyPlanUiState> = _dateIso.flatMapLatest { dateIso ->
        repository.observeTasksForDate(dateIso).map { tasks ->
            DailyPlanUiState(
                dateIso = dateIso,
                tasks = tasks.sortedBy { it.sortOrder },
                doneCount = tasks.count { it.isDone },
                totalCount = tasks.size,
                progressPercent = if (tasks.isEmpty()) 0 else tasks.count { it.isDone } * 100 / tasks.size,
                plannedMinutes = tasks.sumOf { it.durationMinutes ?: 0 },
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyPlanUiState())

    fun prevDay() { _dateIso.value = DateUtils.addDaysIso(_dateIso.value, -1) }
    fun nextDay() { _dateIso.value = DateUtils.addDaysIso(_dateIso.value, 1) }

    fun toggle(taskId: Long, isDone: Boolean) {
        viewModelScope.launch { repository.setDone(taskId, isDone) }
    }

    fun add(title: String, timeLabel: String?, durationMinutes: Int?, priority: TaskPriority, category: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTask(_dateIso.value, title.trim(), timeLabel?.ifBlank { null }, durationMinutes, priority, category)
        }
    }

    fun edit(task: Task, title: String, timeLabel: String?, durationMinutes: Int?, priority: TaskPriority, category: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.updateTask(
                task.copy(
                    title = title.trim(),
                    timeLabel = timeLabel?.ifBlank { null },
                    durationMinutes = durationMinutes,
                    priority = priority,
                    category = category,
                )
            )
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch { repository.delete(task) }
    }
}

fun dailyPlanViewModelFactory() = viewModelFactory {
    initializer { DailyPlanViewModel(ir.parscode.app.di.ServiceLocator.taskRepository) }
}
