package ir.parscode.app.ui.screens.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.parscode.app.domain.model.HabitWithProgress
import ir.parscode.app.domain.repository.HabitRepository
import ir.parscode.app.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.initializer

data class HabitsUiState(
    val habits: List<HabitWithProgress> = emptyList(),
    val bestStreak: Int = 0,
    val averageCompletionPercent: Int = 0,
    val activeCount: Int = 0,
)

class HabitsViewModel(private val repository: HabitRepository) : ViewModel() {

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
    val uiState: StateFlow<HabitsUiState> = _todayIso.flatMapLatest { repository.observeHabitsForDate(it) }
        .map { habits ->
            HabitsUiState(
                habits = habits,
                bestStreak = habits.maxOfOrNull { it.streakDays } ?: 0,
                averageCompletionPercent = if (habits.isEmpty()) 0 else
                    habits.sumOf { it.completionRatePercent } / habits.size,
                activeCount = habits.count { it.isDoneToday },
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HabitsUiState())

    fun toggle(habitId: Long, isDone: Boolean) {
        viewModelScope.launch { repository.toggleDone(habitId, todayIso, isDone) }
    }

    fun edit(habitId: Long, title: String, targetLabel: String?) {
        if (title.isBlank()) return
        viewModelScope.launch { repository.updateHabit(habitId, title.trim(), targetLabel?.trim()?.ifBlank { null }) }
    }

    fun addHabit(title: String, targetLabel: String?) {
        if (title.isBlank()) return
        viewModelScope.launch { repository.addHabit(title.trim(), "target", targetLabel?.trim()) }
    }

    fun archive(habitId: Long) {
        viewModelScope.launch { repository.archive(habitId) }
    }
}

fun habitsViewModelFactory() = androidx.lifecycle.viewmodel.viewModelFactory {
    initializer {
        HabitsViewModel(ir.parscode.app.di.ServiceLocator.habitRepository)
    }
}
