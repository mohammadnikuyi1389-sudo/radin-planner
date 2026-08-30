package ir.parscode.app.ui.screens.dailyplan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.parscode.app.domain.model.Task
import ir.parscode.app.domain.model.TaskPriority
import ir.parscode.app.ui.components.GlowCard
import ir.parscode.app.ui.components.GoldButton
import ir.parscode.app.ui.theme.*
import ir.parscode.app.util.DateUtils

private val CATEGORIES = listOf("درس", "سلامت", "شخصی", "کاری", "پروژه")
private fun categoryIcon(category: String): ImageVector = when (category) {
    "درس" -> Icons.Filled.MenuBook
    "سلامت" -> Icons.Filled.FitnessCenter
    "کاری" -> Icons.Filled.Email
    "پروژه" -> Icons.Filled.EditNote
    else -> Icons.Filled.Person
}
private fun priorityColor(p: TaskPriority) = when (p) {
    TaskPriority.HIGH -> PcDanger
    TaskPriority.MEDIUM -> PcWarning
    TaskPriority.LOW -> PcSuccess
}
private fun priorityLabel(p: TaskPriority) = when (p) {
    TaskPriority.HIGH -> "مهم"
    TaskPriority.MEDIUM -> "متوسط"
    TaskPriority.LOW -> "کم"
}

@Composable
fun DailyPlanScreen(viewModel: DailyPlanViewModel = viewModel(factory = dailyPlanViewModelFactory())) {
    val state by viewModel.uiState.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Task?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(PcBackground)) {
    LazyColumn(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("برنامه‌ی روزانه", style = Typography.headlineMedium, color = PcGold)
                    Text("برنامه‌ریزی، اجرا و مدیریت روز شما", style = Typography.bodySmall, color = PcTextSecondary)
                }
                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = PcGold, modifier = Modifier.size(28.dp))
            }
        }

        item {
            GlowCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "روز بعد", tint = PcGold, modifier = Modifier.clickable { viewModel.nextDay() })
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${DateUtils.weekdayFa(state.dateIso)} ${DateUtils.formatJalaliLong(state.dateIso)}", style = Typography.titleMedium, color = PcTextPrimary)
                        Text(DateUtils.formatGregorianShort(state.dateIso), style = Typography.bodySmall, color = PcTextSecondary)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = "روز قبل", tint = PcGold, modifier = Modifier.clickable { viewModel.prevDay() })
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    title = "زمان برنامه‌ریزی‌شده", icon = Icons.Filled.AccessTime, modifier = Modifier.weight(1f),
                ) {
                    Text(minutesLabel(state.plannedMinutes), style = Typography.titleMedium, color = PcGold)
                    Text("از ${DateUtils.toPersianDigits(state.budgetMinutes / 60)} ساعت", style = Typography.bodySmall, color = PcTextSecondary)
                }
                StatCard(title = "پیشرفت روز", icon = Icons.Filled.TrackChanges, modifier = Modifier.weight(1f)) {
                    ir.parscode.app.ui.components.ProgressRing(percent = state.progressPercent, size = 64.dp, strokeWidth = 6.dp)
                    Text("${DateUtils.toPersianDigits(state.doneCount)} از ${DateUtils.toPersianDigits(state.totalCount)} وظیفه انجام‌شده", style = Typography.bodySmall, color = PcTextSecondary, textAlign = TextAlign.Center)
                }
                StatCard(title = "وظایف", icon = Icons.Filled.Assignment, modifier = Modifier.weight(1f)) {
                    Text("${DateUtils.toPersianDigits(state.doneCount)}/${DateUtils.toPersianDigits(state.totalCount)}", style = Typography.titleMedium, color = PcGold)
                    Text("وظیفه تکمیل شده", style = Typography.bodySmall, color = PcTextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        repeat(state.totalCount) { i ->
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (i < state.doneCount) PcGold else PcTextDisabled))
                        }
                    }
                }
            }
        }

        item { Text("وظایف امروز", style = Typography.titleLarge, color = PcGold) }

        if (state.tasks.isEmpty()) {
            item { GlowCard(modifier = Modifier.fillMaxWidth()) { Text("وظیفه‌ای برای این روز ثبت نشده.", color = PcTextSecondary) } }
        } else {
            items(state.tasks, key = { it.id }) { task ->
                TaskCard(task = task, onToggle = { viewModel.toggle(task.id, it) }, onEdit = { editing = task }, onDelete = { viewModel.delete(task) })
            }
        }

        item {
            GlowCard(modifier = Modifier.fillMaxWidth()) {
                Text("برنامه زمانی امروز", style = Typography.titleMedium, color = PcGold)
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    val points = buildList {
                        add("شروع روز" to DAY_START_LABEL)
                        addAll(state.tasks.mapNotNull { t -> t.timeLabel?.let { t.title to it } }.sortedBy { it.second })
                        add("پایان روز" to DAY_END_LABEL)
                    }
                    items(points) { (label, time) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(time, style = Typography.bodySmall, color = PcTextSecondary)
                            Text(label, style = Typography.bodySmall, color = PcTextPrimary)
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {
        TaskDialog(title = "افزودن وظیفه", confirmLabel = "افزودن", onDismiss = { showAdd = false }) { t, time, dur, prio, cat ->
            viewModel.add(t, time, dur, prio, cat); showAdd = false
        }
    }
    editing?.let { t ->
        TaskDialog(
            title = "ویرایش وظیفه", confirmLabel = "ذخیره",
            initialTitle = t.title, initialTime = t.timeLabel ?: "", initialDuration = t.durationMinutes?.toString() ?: "",
            initialPriority = t.priority, initialCategory = t.category,
            onDismiss = { editing = null },
        ) { title, time, dur, prio, cat -> viewModel.edit(t, title, time, dur, prio, cat); editing = null }
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        GoldButton(text = "افزودن وظیفه جدید", onClick = { showAdd = true })
    }
    } // end root Column
}

@Composable
private fun StatCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    GlowCard(modifier = modifier, contentPadding = PaddingValues(10.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(title, style = Typography.bodySmall, color = PcTextSecondary)
            Icon(icon, contentDescription = null, tint = PcGold, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
private fun TaskCard(task: Task, onToggle: (Boolean) -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    GlowCard(modifier = Modifier.fillMaxWidth().clickable { onEdit() }, contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Box {
                Icon(Icons.Filled.MoreVert, contentDescription = "منو", tint = PcTextSecondary, modifier = Modifier.clickable { showMenu = true })
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(text = { Text("ویرایش") }, onClick = { showMenu = false; onEdit() })
                    DropdownMenuItem(text = { Text("حذف") }, onClick = { showMenu = false; onDelete() })
                }
            }
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(task.title, style = Typography.bodyLarge, color = if (task.isDone) PcTextSecondary else PcTextPrimary, textDecoration = if (task.isDone) TextDecoration.LineThrough else null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(categoryIcon(task.category), contentDescription = null, tint = PcGoldMuted, modifier = Modifier.size(16.dp))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Tag(priorityLabel(task.priority), priorityColor(task.priority))
                    Tag(task.category, PcSurfaceRaised, PcTextSecondary)
                }
                if (task.timeLabel != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(task.timeLabel, style = Typography.bodySmall, color = PcTextSecondary)
                        Icon(Icons.Filled.AccessTime, contentDescription = null, tint = PcTextSecondary, modifier = Modifier.size(12.dp).padding(start = 4.dp))
                    }
                    if (task.durationMinutes != null) Text(minutesLabel(task.durationMinutes), style = Typography.bodySmall, color = PcTextSecondary)
                }
            }
            Icon(
                imageVector = if (task.isDone) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                contentDescription = null,
                tint = if (task.isDone) PcGold else PcTextSecondary,
                modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickable { onToggle(!task.isDone) },
            )
        }
    }
}

@Composable
private fun Tag(text: String, bg: androidx.compose.ui.graphics.Color, fg: androidx.compose.ui.graphics.Color = PcBackground) {
    Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(bg).padding(horizontal = 8.dp, vertical = 2.dp)) {
        Text(text, style = Typography.bodySmall, color = fg)
    }
}

private fun minutesLabel(total: Int): String {
    val h = total / 60
    val m = total % 60
    return when {
        h > 0 && m > 0 -> "${DateUtils.toPersianDigits(h)} ساعت ${DateUtils.toPersianDigits(m)} دقیقه"
        h > 0 -> "${DateUtils.toPersianDigits(h)} ساعت"
        else -> "${DateUtils.toPersianDigits(m)} دقیقه"
    }
}

@Composable
private fun TaskDialog(
    title: String,
    confirmLabel: String,
    initialTitle: String = "",
    initialTime: String = "",
    initialDuration: String = "",
    initialPriority: TaskPriority = TaskPriority.MEDIUM,
    initialCategory: String = "شخصی",
    onDismiss: () -> Unit,
    onConfirm: (String, String?, Int?, TaskPriority, String) -> Unit,
) {
    var t by remember { mutableStateOf(initialTitle) }
    var time by remember { mutableStateOf(initialTime) }
    var duration by remember { mutableStateOf(initialDuration) }
    var priority by remember { mutableStateOf(initialPriority) }
    var category by remember { mutableStateOf(initialCategory) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PcSurface,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(value = t, onValueChange = { t = it }, label = { Text("عنوان وظیفه") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("ساعت (مثلا ۰۸:۰۰)") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("مدت (دقیقه)") })
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TaskPriority.entries.forEach { p ->
                        FilterChip(selected = p == priority, onClick = { priority = p }, label = { Text(priorityLabel(p)) })
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CATEGORIES.forEach { cat ->
                        FilterChip(
                            selected = cat == category, onClick = { category = cat }, label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PcGold, selectedLabelColor = PcBackground, containerColor = PcSurfaceRaised),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(t, time.ifBlank { null }, duration.toIntOrNull(), priority, category) }, enabled = t.isNotBlank()) {
                Text(confirmLabel, color = PcGold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
    )
}
