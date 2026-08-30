package ir.parscode.app.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.parscode.app.domain.model.Task
import ir.parscode.app.ui.components.GlowCard
import ir.parscode.app.ui.components.GoldButton
import ir.parscode.app.ui.components.ProgressRing
import ir.parscode.app.ui.theme.*
import ir.parscode.app.util.DateUtils

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(factory = dashboardViewModelFactory()),
    onNavigate: (String) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PcBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column {
                Text("سلام محمد عزیز!", style = Typography.headlineMedium, color = PcGold)
                Text("به دنیای تمرکز و پیشرفت خوش آمدید", style = Typography.bodyMedium, color = PcTextSecondary)
            }
        }

        item {
            GlowCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    ProgressRing(percent = state.progressPercent)
                    Column(horizontalAlignment = Alignment.End) {
                        Text("هدف روزانه", style = Typography.titleMedium, color = PcGold)
                        Text(state.todayLabel, style = Typography.bodySmall, color = PcTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${DateUtils.toPersianDigits(state.doneCount)} از ${DateUtils.toPersianDigits(state.totalCount)} وظیفه انجام شده",
                            style = Typography.bodyMedium,
                            color = PcTextPrimary,
                        )
                    }
                }
            }
        }

        item { GoldButton(text = "افزودن وظیفه", onClick = { showAddDialog = true }) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                QuickAction("برنامه", ir.parscode.app.ui.navigation.NavRoutes.DAILY_PLAN, onNavigate, Modifier.weight(1f))
                QuickAction("اهداف", ir.parscode.app.ui.navigation.NavRoutes.GOALS, onNavigate, Modifier.weight(1f))
                QuickAction("کتابخانه", ir.parscode.app.ui.navigation.NavRoutes.LIBRARY, onNavigate, Modifier.weight(1f))
                QuickAction("آمار", ir.parscode.app.ui.navigation.NavRoutes.PROFILE, onNavigate, Modifier.weight(1f))
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("وظایف امروز", style = Typography.titleLarge, color = PcGold)
                Text("${DateUtils.toPersianDigits(state.totalCount - state.doneCount)} مورد باقی‌مانده", style = Typography.bodySmall, color = PcTextSecondary)
            }
        }

        if (state.tasks.isEmpty()) {
            item {
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Text("هنوز وظیفه‌ای برای امروز ثبت نکرده‌اید.", style = Typography.bodyMedium, color = PcTextSecondary)
                }
            }
        } else {
            items(state.tasks) { task ->
                TaskRow(task = task, onToggle = { viewModel.toggleTask(task.id, it) }, onEdit = { editingTask = it })
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, time ->
                viewModel.addTask(title, time)
                showAddDialog = false
            },
        )
    }

    editingTask?.let { t ->
        AddTaskDialog(
            initialTitle = t.title,
            initialTime = t.timeLabel ?: "",
            confirmLabel = "ذخیره",
            dialogTitle = "ویرایش وظیفه",
            onDismiss = { editingTask = null },
            onConfirm = { title, time ->
                viewModel.editTask(t, title, time)
                editingTask = null
            },
        )
    }
}

@Composable
private fun QuickAction(label: String, route: String, onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    GlowCard(
        modifier = modifier.clickable { onNavigate(route) },
        contentPadding = PaddingValues(vertical = 12.dp),
    ) {
        Text(label, style = Typography.bodySmall, color = PcGold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@Composable
private fun TaskRow(task: Task, onToggle: (Boolean) -> Unit, onEdit: (Task) -> Unit) {
    GlowCard(
        modifier = Modifier.fillMaxWidth().clickable { onEdit(task) },
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = Typography.bodyLarge,
                    color = if (task.isDone) PcTextSecondary else PcTextPrimary,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                )
                if (task.timeLabel != null) {
                    Text(task.timeLabel, style = Typography.bodySmall, color = PcTextSecondary)
                }
            }
            Icon(
                imageVector = if (task.isDone) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (task.isDone) PcGold else PcTextSecondary,
                modifier = Modifier.clip(RoundedCornerShape(50)).clickable { onToggle(!task.isDone) },
            )
        }
    }
}
