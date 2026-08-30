package ir.parscode.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.parscode.app.domain.model.Task
import ir.parscode.app.domain.repository.HabitRepository
import ir.parscode.app.domain.repository.TaskRepository
import ir.parscode.app.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.initializer

data class DashboardUiState(
    val todayIso: String = DateUtils.todayIso(),
    val todayLabel: String = "",
    val tasks: List<Task> = emptyList(),
    val doneCount: Int = 0,
    val totalCount: Int = 0,
    val progressPercent: Int = 0,
    val isLoading: Boolean = true,
)

class DashboardViewModel(
    private val taskRepository: TaskRepository,
    habitRepository: HabitRepository,
) : ViewModel() {

    private val _todayIso = MutableStateFlow(DateUtils.todayIso())
    private val todayIso get() = _todayIso.value

    init {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(60_000)
                _todayIso.value = DateUtils.todayIso()
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = _todayIso.flatMapLatest { today ->
        combine(
            taskRepository.observeTasksForDate(today),
            habitRepository.observeHabitsForDate(today),
        ) { tasks, habits ->
        // Overall daily progress blends tasks and habits so the ring on the
        // dashboard reflects the whole day, not just one list.
        val doneUnits = tasks.count { it.isDone } + habits.count { it.isDoneToday }
        val totalUnits = tasks.size + habits.size
        val percent = if (totalUnits == 0) 0 else (doneUnits * 100 / totalUnits)
        DashboardUiState(
            todayIso = todayIso,
            todayLabel = "${DateUtils.weekdayFa(todayIso)} ${DateUtils.formatJalaliLong(todayIso)}",
            tasks = tasks.sortedBy { it.sortOrder },
            doneCount = tasks.count { it.isDone },
            totalCount = tasks.size,
            progressPercent = percent,
            isLoading = false,
        )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    fun toggleTask(taskId: Long, isDone: Boolean) {
        viewModelScope.launch { taskRepository.setDone(taskId, isDone) }
    }

    fun editTask(task: Task, title: String, timeLabel: String?) {
        if (title.isBlank()) return
        viewModelScope.launch { taskRepository.updateTask(task.copy(title = title.trim(), timeLabel = timeLabel?.trim()?.ifBlank { null })) }
    }

    fun addTask(title: String, timeLabel: String?) {
        if (title.isBlank()) return
        viewModelScope.launch {
            taskRepository.addTask(
                dateIso = todayIso,
                title = title.trim(),
                timeLabel = timeLabel?.trim()?.ifBlank { null },
                durationMinutes = null,
                priority = ir.parscode.app.domain.model.TaskPriority.MEDIUM,
                category = "شخصی",
            )
        }
    }
}

fun dashboardViewModelFactory() = androidx.lifecycle.viewmodel.viewModelFactory {
    initializer {
        DashboardViewModel(
            ir.parscode.app.di.ServiceLocator.taskRepository,
            ir.parscode.app.di.ServiceLocator.habitRepository,
        )
    }
}
