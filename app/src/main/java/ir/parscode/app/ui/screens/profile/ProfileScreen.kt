package ir.parscode.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ir.parscode.app.di.ServiceLocator
import ir.parscode.app.ui.components.GlowCard
import ir.parscode.app.ui.theme.*
import ir.parscode.app.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileStats(val activeHabits: Int = 0, val bestStreak: Int = 0, val doneTasksToday: Int = 0, val totalTasksToday: Int = 0)

class ProfileViewModel : ViewModel() {
    private val _today = MutableStateFlow(DateUtils.todayIso())

    init {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(60_000)
                _today.value = DateUtils.todayIso()
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val stats: StateFlow<ProfileStats> = _today.flatMapLatest { today ->
        combine(
            ServiceLocator.habitRepository.observeHabitsForDate(today),
            ServiceLocator.taskRepository.observeTasksForDate(today),
        ) { habits, tasks ->
        ProfileStats(
            activeHabits = habits.count { it.isDoneToday },
            bestStreak = habits.maxOfOrNull { it.streakDays } ?: 0,
            doneTasksToday = tasks.count { it.isDone },
            totalTasksToday = tasks.size,
        )
    }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileStats())
}
fun profileViewModelFactory() = viewModelFactory { initializer { ProfileViewModel() } }

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel(factory = profileViewModelFactory())) {
    val s by viewModel.stats.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().background(PcBackground).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(PcSurfaceRaised), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = PcGold, modifier = Modifier.size(40.dp))
        }
        Text("محمد نیکویی", style = Typography.titleLarge, color = PcTextPrimary)
        Text("کاربر حرفه‌ای", style = Typography.bodySmall, color = PcGoldMuted)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            GlowCard(modifier = Modifier.weight(1f)) {
                Text("${DateUtils.toPersianDigits(s.bestStreak)}", style = Typography.titleMedium, color = PcGold)
                Text("رکورد متوالی", style = Typography.bodySmall, color = PcTextSecondary)
            }
            GlowCard(modifier = Modifier.weight(1f)) {
                Text("${DateUtils.toPersianDigits(s.activeHabits)}", style = Typography.titleMedium, color = PcGold)
                Text("عادت امروز", style = Typography.bodySmall, color = PcTextSecondary)
            }
            GlowCard(modifier = Modifier.weight(1f)) {
                Text("${DateUtils.toPersianDigits(s.doneTasksToday)}/${DateUtils.toPersianDigits(s.totalTasksToday)}", style = Typography.titleMedium, color = PcGold)
                Text("وظایف امروز", style = Typography.bodySmall, color = PcTextSecondary)
            }
        }
    }
}
