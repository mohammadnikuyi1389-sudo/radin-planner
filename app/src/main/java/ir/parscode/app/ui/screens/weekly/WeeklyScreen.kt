package ir.parscode.app.ui.screens.weekly

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import ir.parscode.app.domain.model.Task
import ir.parscode.app.ui.components.GlowCard
import ir.parscode.app.ui.theme.*
import ir.parscode.app.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeeklyViewModel : ViewModel() {
    private val repo = ServiceLocator.taskRepository
    private val _selectedDate = MutableStateFlow(DateUtils.todayIso())
    val selectedDate: StateFlow<String> = _selectedDate

    val tasks: StateFlow<List<Task>> = _selectedDate
        .flatMapLatest { repo.observeTasksForDate(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDate(iso: String) { _selectedDate.value = iso }
    fun toggle(id: Long, done: Boolean) { viewModelScope.launch { repo.setDone(id, done) } }
}
fun weeklyViewModelFactory() = viewModelFactory { initializer { WeeklyViewModel() } }

@Composable
fun WeeklyScreen(viewModel: WeeklyViewModel = viewModel(factory = weeklyViewModelFactory())) {
    val selected by viewModel.selectedDate.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val today = DateUtils.todayIso()
    // Saturday-start week containing today
    val weekDates = remember(today) {
        val todayDow = DateUtils.parseIso(today).dayOfWeek.value // Mon=1..Sun=7
        val sinceSaturday = ((todayDow - 6) % 7 + 7) % 7
        (0..6).map { DateUtils.addDaysIso(today, (it - sinceSaturday).toLong()) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PcBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Text("نمای هفتگی", style = Typography.headlineMedium, color = PcGold) }
        item {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                weekDates.forEach { d ->
                    val isSel = d == selected
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) PcGold else PcSurface)
                            .clickable { viewModel.selectDate(d) }
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                    ) {
                        Text(DateUtils.weekdayFa(d).take(2), color = if (isSel) PcBackground else PcTextSecondary, style = Typography.bodySmall)
                        Text(DateUtils.jalaliDayOfMonth(d), color = if (isSel) PcBackground else PcTextPrimary, style = Typography.bodyMedium)
                    }
                }
            }
        }
        item { Text(DateUtils.formatJalaliLong(selected), style = Typography.titleMedium, color = PcTextSecondary) }
        if (tasks.isEmpty()) {
            item { GlowCard(modifier = Modifier.fillMaxWidth()) { Text("وظیفه‌ای برای این روز نیست.", color = PcTextSecondary) } }
        } else {
            items(tasks, key = { it.id }) { t ->
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(t.title, color = if (t.isDone) PcTextSecondary else PcTextPrimary)
                        androidx.compose.material3.Checkbox(
                            checked = t.isDone,
                            onCheckedChange = { viewModel.toggle(t.id, it) },
                            colors = androidx.compose.material3.CheckboxDefaults.colors(checkedColor = PcGold),
                        )
                    }
                }
            }
        }
    }
}
