package ir.parscode.app.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ir.parscode.app.data.local.entity.GoalEntity
import ir.parscode.app.di.ServiceLocator
import ir.parscode.app.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalsViewModel : ViewModel() {
    private val dao = ServiceLocator.goalDao
    val goals: StateFlow<List<GoalEntity>> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun add(title: String, deadline: String?) {
        if (title.isBlank()) return
        viewModelScope.launch {
            dao.upsert(GoalEntity(title = title.trim(), deadlineIso = deadline?.ifBlank { null }, createdDateIso = DateUtils.todayIso()))
        }
    }

    fun updateProgress(goal: GoalEntity, percent: Int) {
        viewModelScope.launch { dao.update(goal.copy(progressPercent = percent, isCompleted = percent >= 100)) }
    }

    fun edit(goal: GoalEntity, title: String, deadline: String?) {
        if (title.isBlank()) return
        viewModelScope.launch { dao.update(goal.copy(title = title.trim(), deadlineIso = deadline?.ifBlank { null })) }
    }

    fun delete(goal: GoalEntity) {
        viewModelScope.launch { dao.delete(goal) }
    }
}

fun goalsViewModelFactory() = viewModelFactory { initializer { GoalsViewModel() } }
