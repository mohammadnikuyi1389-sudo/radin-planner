package ir.parscode.app.ui.screens.habits

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.parscode.app.domain.model.HabitWithProgress
import ir.parscode.app.ui.components.GlowCard
import ir.parscode.app.ui.components.GoldButton
import ir.parscode.app.ui.theme.*
import ir.parscode.app.util.DateUtils

@Composable
fun HabitsScreen(viewModel: HabitsViewModel = viewModel(factory = habitsViewModelFactory())) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingHabit by remember { mutableStateOf<HabitWithProgress?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PcBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { Text("عادت‌ها", style = Typography.headlineMedium, color = PcGold) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatChip(label = "رکورد متوالی", value = "${DateUtils.toPersianDigits(state.bestStreak)} روز", modifier = Modifier.weight(1f))
                StatChip(label = "میانگین تکمیل", value = "${DateUtils.toPersianDigits(state.averageCompletionPercent)}٪", modifier = Modifier.weight(1f))
                StatChip(label = "انجام‌شده امروز", value = "${DateUtils.toPersianDigits(state.activeCount)}/${DateUtils.toPersianDigits(state.habits.size)}", modifier = Modifier.weight(1f))
            }
        }

        item { GoldButton(text = "افزودن عادت جدید", onClick = { showAddDialog = true }) }

        if (state.habits.isEmpty()) {
            item {
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text("هنوز عادتی اضافه نکرده‌اید.", style = Typography.bodyMedium, color = PcTextSecondary)
                }
            }
        } else {
            items(state.habits, key = { it.habit.id }) { hp ->
                HabitRow(
                    hp = hp,
                    onToggle = { viewModel.toggle(hp.habit.id, it) },
                    onArchive = { viewModel.archive(hp.habit.id) },
                    onEdit = { editingHabit = hp },
                )
            }
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, target ->
                viewModel.addHabit(title, target)
                showAddDialog = false
            },
        )
    }

    editingHabit?.let { hp ->
        AddHabitDialog(
            initialTitle = hp.habit.title,
            initialTarget = hp.habit.targetLabel ?: "",
            dialogTitle = "ویرایش عادت",
            confirmLabel = "ذخیره",
            onDismiss = { editingHabit = null },
            onConfirm = { title, target ->
                viewModel.edit(hp.habit.id, title, target)
                editingHabit = null
            },
        )
    }
}

@Composable
private fun StatChip(label: String, value: String, modifier: Modifier = Modifier) {
    GlowCard(modifier = modifier, contentPadding = PaddingValues(10.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(value, style = Typography.titleMedium, color = PcGold)
            Text(label, style = Typography.bodySmall, color = PcTextSecondary)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HabitRow(hp: HabitWithProgress, onToggle: (Boolean) -> Unit, onArchive: () -> Unit, onEdit: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }
    if (showConfirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showConfirm = false },
            containerColor = PcSurface,
            title = { Text("حذف «${hp.habit.title}»؟") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { onArchive(); showConfirm = false }) {
                    Text("حذف", color = PcGold)
                }
            },
            dismissButton = { androidx.compose.material3.TextButton(onClick = { showConfirm = false }) { Text("انصراف") } },
        )
    }
    GlowCard(
        modifier = Modifier.fillMaxWidth()
            .combinedClickable(onClick = onEdit, onLongClick = { showConfirm = true }),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = if (hp.isDoneToday) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (hp.isDoneToday) PcGold else PcTextSecondary,
                modifier = Modifier.clip(RoundedCornerShape(50)).clickable { onToggle(!hp.isDoneToday) },
            )
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(hp.habit.title, style = Typography.bodyLarge, color = PcTextPrimary)
                if (hp.habit.targetLabel != null) {
                    Text(hp.habit.targetLabel, style = Typography.bodySmall, color = PcTextSecondary)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${DateUtils.toPersianDigits(hp.streakDays)} روز", style = Typography.bodySmall, color = PcGoldMuted)
                Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = PcGoldMuted, modifier = Modifier.size(16.dp))
            }
        }
    }
}
