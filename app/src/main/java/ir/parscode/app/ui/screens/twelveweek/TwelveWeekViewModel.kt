package ir.parscode.app.ui.screens.twelveweek

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ir.parscode.app.data.local.entity.GoalEntity
import ir.parscode.app.data.local.entity.TaskEntity
import ir.parscode.app.data.local.entity.WeekEntity
import ir.parscode.app.di.ServiceLocator
import ir.parscode.app.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TwelveWeekViewModel : ViewModel() {
    private val weekDao = ServiceLocator.weekDao
    private val goalDao = ServiceLocator.goalDao
    private val taskDao = ServiceLocator.taskDao

    val weeks: StateFlow<List<WeekEntity>> = weekDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedWeekId = MutableStateFlow<Long?>(null)
    val selectedWeekId: StateFlow<Long?> = _selectedWeekId

    fun selectWeek(id: Long) { _selectedWeekId.value = id }

    /** Once weeks load, default to the current "active" week (first one in progress). */
    fun selectDefaultIfNeeded(list: List<WeekEntity>) {
        if (_selectedWeekId.value != null || list.isEmpty()) return
        val active = list.firstOrNull { it.progressPercent in 1..99 } ?: list.first()
        _selectedWeekId.value = active.id
    }

    val goalsForSelected: StateFlow<List<GoalEntity>> = _selectedWeekId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else goalDao.observeByWeek(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasksForSelected: StateFlow<List<TaskEntity>> = _selectedWeekId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else taskDao.observeForWeek(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addWeek() {
        viewModelScope.launch {
            val next = (weeks.value.maxOfOrNull { it.weekNumber } ?: 0) + 1
            weekDao.upsert(WeekEntity(weekNumber = next, title = "هفته جدید", progressPercent = 0, status = ""))
        }
    }

    fun addGoal(title: String, desc: String?) {
        val weekId = _selectedWeekId.value ?: return
        if (title.isBlank()) return
        viewModelScope.launch {
            goalDao.upsert(
                GoalEntity(title = title.trim(), description = desc?.ifBlank { null }, createdDateIso = DateUtils.todayIso(), weekId = weekId)
            )
        }
    }
}

fun twelveWeekViewModelFactory() = viewModelFactory { initializer { TwelveWeekViewModel() } }
