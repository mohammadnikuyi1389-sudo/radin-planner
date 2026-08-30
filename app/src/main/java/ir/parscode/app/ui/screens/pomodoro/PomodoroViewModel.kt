package ir.parscode.app.ui.screens.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ir.parscode.app.data.local.entity.PomodoroSessionEntity
import ir.parscode.app.di.ServiceLocator
import ir.parscode.app.util.DateUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PomodoroUiState(
    val totalSeconds: Int = 25 * 60,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val kind: String = "FOCUS",
    val completedTodayCount: Int = 0,
)

class PomodoroViewModel : ViewModel() {
    private val dao = ServiceLocator.pomodoroDao
    private val _todayIso = MutableStateFlow(DateUtils.todayIso())
    private val todayIso get() = _todayIso.value
    private val _state = MutableStateFlow(PomodoroUiState())
    val state: StateFlow<PomodoroUiState> = _state

    init {
        viewModelScope.launch {
            while (true) {
                delay(60_000)
                _todayIso.value = DateUtils.todayIso()
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val historyCount: StateFlow<Int> = _todayIso.flatMapLatest { dao.observeForDate(it) }
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private var job: kotlinx.coroutines.Job? = null

    fun selectDuration(minutes: Int, kind: String) {
        if (_state.value.isRunning) return
        _state.value = PomodoroUiState(totalSeconds = minutes * 60, remainingSeconds = minutes * 60, kind = kind)
    }

    fun start() {
        if (_state.value.isRunning) return
        _state.value = _state.value.copy(isRunning = true)
        job = viewModelScope.launch {
            while (isActive && _state.value.remainingSeconds > 0 && _state.value.isRunning) {
                delay(1000)
                _state.value = _state.value.copy(remainingSeconds = _state.value.remainingSeconds - 1)
            }
            if (_state.value.remainingSeconds <= 0) onComplete()
        }
    }

    fun pause() {
        job?.cancel()
        _state.value = _state.value.copy(isRunning = false)
    }

    fun reset() {
        job?.cancel()
        _state.value = _state.value.copy(remainingSeconds = _state.value.totalSeconds, isRunning = false)
    }

    private fun onComplete() {
        viewModelScope.launch {
            dao.insert(
                PomodoroSessionEntity(
                    dateIso = todayIso,
                    durationMinutes = _state.value.totalSeconds / 60,
                    kind = _state.value.kind,
                    completedAtMillis = System.currentTimeMillis(),
                )
            )
        }
        _state.value = _state.value.copy(isRunning = false, remainingSeconds = _state.value.totalSeconds)
    }
}
fun pomodoroViewModelFactory() = viewModelFactory { initializer { PomodoroViewModel() } }
