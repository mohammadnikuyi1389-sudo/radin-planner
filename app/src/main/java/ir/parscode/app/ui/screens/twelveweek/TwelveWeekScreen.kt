package ir.parscode.app.ui.screens.twelveweek

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.parscode.app.data.local.entity.GoalEntity
import ir.parscode.app.data.local.entity.TaskEntity
import ir.parscode.app.data.local.entity.WeekEntity
import ir.parscode.app.ui.components.GlowCard
import ir.parscode.app.ui.components.GoldButton
import ir.parscode.app.ui.components.ProgressRing
import ir.parscode.app.ui.navigation.NavRoutes
import ir.parscode.app.ui.theme.*
import ir.parscode.app.util.DateUtils
import ir.parscode.app.util.ProgressStatus

@Composable
fun TwelveWeekScreen(
    viewModel: TwelveWeekViewModel = viewModel(factory = twelveWeekViewModelFactory()),
    onNavigate: (String) -> Unit = {},
) {
    val weeks by viewModel.weeks.collectAsState()
    val selectedWeekId by viewModel.selectedWeekId.collectAsState()
    val goals by viewModel.goalsForSelected.collectAsState()
    val tasks by viewModel.tasksForSelected.collectAsState()
    LaunchedEffect(weeks) { viewModel.selectDefaultIfNeeded(weeks) }
    val selectedWeek = weeks.firstOrNull { it.id == selectedWeekId }
    var showAddGoal by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(PcBackground).padding(16.dp)) {
        // Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("اهداف و برنامه ۱۲ هفته‌ای", style = Typography.headlineMedium, color = PcGold)
                Text("ارتباط اهداف با برنامه‌های هفته", style = Typography.bodySmall, color = PcTextSecondary)
            }
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(PcSurfaceRaised), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.TrackChanges, contentDescription = null, tint = PcGold)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))

        // Segmented tabs
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50)).background(PcSurface).border(1.dp, PcBorder, RoundedCornerShape(50)).padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            SegmentTab("نقشه ارتباط", Icons.Filled.AccountTree, selected = false, modifier = Modifier.weight(1f)) { onNavigate(NavRoutes.WEEKLY) }
            SegmentTab("برنامه ۱۲ هفته‌ای", Icons.Filled.CalendarMonth, selected = true, modifier = Modifier.weight(1f)) {}
            SegmentTab("اهداف من", Icons.Filled.TrackChanges, selected = false, modifier = Modifier.weight(1f)) { onNavigate(NavRoutes.GOALS) }
        }
        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Left: weeks list
            GlowCard(modifier = Modifier.weight(1f).fillMaxHeight(), contentPadding = PaddingValues(12.dp)) {
                Text("هفته‌ها", style = Typography.titleMedium, color = PcTextPrimary, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(weeks, key = { it.id }) { w ->
                        WeekRow(w, selected = w.id == selectedWeekId) { viewModel.selectWeek(w.id) }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                GoldButton(text = "افزودن هفته جدید", onClick = { viewModel.addWeek() })
            }

            // Right: selected week detail
            GlowCard(modifier = Modifier.weight(2.2f).fillMaxHeight(), contentPadding = PaddingValues(12.dp)) {
                if (selectedWeek == null) {
                    Text("هفته‌ای انتخاب نشده", color = PcTextSecondary)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        item { WeekDetailHeader(selectedWeek) }
                        item { WeekProgressCard(selectedWeek, goals, tasks) }
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("اهداف این هفته", style = Typography.titleMedium, color = PcTextPrimary)
                                TextButton(onClick = { showAddGoal = true }) { Text("افزودن هدف  +", color = PcGold, style = Typography.bodySmall) }
                            }
                        }
                        if (goals.isEmpty()) {
                            item { Text("هنوز هدفی برای این هفته ثبت نشده.", color = PcTextSecondary, style = Typography.bodySmall) }
                        } else {
                            items(goals, key = { it.id }) { g -> GoalRow(g, tasks.count { it.goalId == g.id }) }
                        }
                        item {
                            OutlinedButton(onClick = { onNavigate(NavRoutes.GOALS) }, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, PcBorder)) {
                                Text("مشاهده همه اهداف این هفته", color = PcGoldMuted, style = Typography.bodySmall)
                            }
                        }
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("وظایف مربوط به این هفته", style = Typography.titleMedium, color = PcTextPrimary)
                                TextButton(onClick = { onNavigate(NavRoutes.DAILY_PLAN) }) { Text("مشاهده همه", color = PcGoldMuted, style = Typography.bodySmall) }
                            }
                        }
                        item { TaskStatsRow(tasks) }
                        item { GoldButton(text = "مشاهده همه وظایف این هفته", onClick = { onNavigate(NavRoutes.DAILY_PLAN) }, showPlusIcon = false) }
                    }
                }
            }
        }
    }

    if (showAddGoal) {
        AddGoalDialog(onDismiss = { showAddGoal = false }) { t, d -> viewModel.addGoal(t, d); showAddGoal = false }
    }
}

@Composable
private fun SegmentTab(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) PcSurfaceRaised else Color.Transparent)
            .border(if (selected) 1.dp else 0.dp, if (selected) PcBorderBright else Color.Transparent, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) PcGold else PcTextSecondary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = Typography.bodySmall, color = if (selected) PcGold else PcTextSecondary, maxLines = 1)
    }
}

@Composable
private fun WeekRow(w: WeekEntity, selected: Boolean, onClick: () -> Unit) {
    val color = ProgressStatus.color(w.progressPercent)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) PcSurfaceRaised else Color.Transparent)
            .border(if (selected) 1.dp else 0.dp, PcBorderBright, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            ProgressRing(percent = w.progressPercent, size = 44.dp, strokeWidth = 4.dp, ringColor = color, textStyle = Typography.bodySmall)
            if (w.progressPercent >= 100) {
                Box(modifier = Modifier.size(14.dp).align(Alignment.TopEnd).clip(CircleShape).background(PcSuccess), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = PcBackground, modifier = Modifier.size(10.dp))
                }
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text("هفته ${DateUtils.toPersianDigits(w.weekNumber)}", style = Typography.bodyMedium, color = PcTextPrimary)
            Text(w.title, style = Typography.bodySmall, color = PcTextSecondary, maxLines = 1)
        }
    }
}

@Composable
private fun WeekDetailHeader(w: WeekEntity) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("هفته ${DateUtils.toPersianDigits(w.weekNumber)}: ${w.title}", style = Typography.titleLarge, color = PcTextPrimary)
        Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = PcGoldMuted)
    }
}

@Composable
private fun WeekProgressCard(w: WeekEntity, goals: List<GoalEntity>, tasks: List<TaskEntity>) {
    val completedGoals = goals.count { it.progressPercent >= 100 }
    val doneTasks = tasks.count { it.taskStatus == "DONE" }
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(PcSurface).padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("درصد پیشرفت هفته", style = Typography.bodySmall, color = PcTextSecondary)
            Text("${DateUtils.toPersianDigits(w.progressPercent)}٪", style = Typography.headlineMedium, color = PcGold)
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { w.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                color = PcGold, trackColor = PcBorder,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(horizontalAlignment = Alignment.End) {
            StatLine(Icons.Filled.TrackChanges, "${DateUtils.toPersianDigits(completedGoals)} از ${DateUtils.toPersianDigits(goals.size)} هدف تکمیل شده")
            Spacer(modifier = Modifier.height(8.dp))
            StatLine(Icons.Filled.Assignment, "${DateUtils.toPersianDigits(doneTasks)} از ${DateUtils.toPersianDigits(tasks.size)} وظیفه انجام شده")
        }
    }
}

@Composable
private fun StatLine(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text, style = Typography.bodySmall, color = PcTextSecondary)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(icon, contentDescription = null, tint = PcGoldMuted, modifier = Modifier.size(14.dp))
    }
}

@Composable
private fun GoalRow(g: GoalEntity, taskCount: Int) {
    val statusColor = ProgressStatus.color(g.progressPercent)
    GlowCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            ProgressRing(percent = g.progressPercent, size = 48.dp, strokeWidth = 4.dp, ringColor = statusColor, textStyle = Typography.bodySmall)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(g.title, style = Typography.bodyMedium, color = PcTextPrimary)
                    Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(statusColor.copy(alpha = 0.16f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text(ProgressStatus.labelFa(g.progressPercent), style = Typography.bodySmall, color = statusColor)
                    }
                }
                if (g.description != null) Text(g.description, style = Typography.bodySmall, color = PcTextSecondary, maxLines = 1)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = PcTextDisabled, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${DateUtils.toPersianDigits(taskCount)} وظیفه", style = Typography.bodySmall, color = PcTextDisabled)
                }
            }
            Icon(Icons.Filled.ChevronLeft, contentDescription = null, tint = PcTextSecondary)
        }
    }
}

@Composable
private fun TaskStatsRow(tasks: List<TaskEntity>) {
    val notStarted = tasks.count { it.taskStatus == "TODO" }
    val pending = tasks.count { it.taskStatus == "PENDING" }
    val done = tasks.count { it.taskStatus == "DONE" }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatChip("انجام نشده", notStarted, PcDanger, Modifier.weight(1f))
        StatChip("در انتظار", pending, PcGold, Modifier.weight(1f))
        StatChip("انجام شده", done, PcSuccess, Modifier.weight(1f))
        StatChip("کل وظایف", tasks.size, PcTextPrimary, Modifier.weight(1f))
    }
}

@Composable
private fun StatChip(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(PcSurface).border(1.dp, PcBorder, RoundedCornerShape(12.dp)).padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, style = Typography.bodySmall, color = PcTextSecondary)
        Text(DateUtils.toPersianDigits(count), style = Typography.titleLarge, color = color)
    }
}

@Composable
private fun AddGoalDialog(onDismiss: () -> Unit, onConfirm: (String, String?) -> Unit) {
    var t by remember { mutableStateOf("") }
    var d by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PcSurface,
        title = { Text("افزودن هدف") },
        text = {
            Column {
                OutlinedTextField(value = t, onValueChange = { t = it }, label = { Text("عنوان هدف") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = d, onValueChange = { d = it }, label = { Text("توضیحات (اختیاری)") })
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(t, d.ifBlank { null }) }, enabled = t.isNotBlank()) { Text("افزودن", color = PcGold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
    )
}
