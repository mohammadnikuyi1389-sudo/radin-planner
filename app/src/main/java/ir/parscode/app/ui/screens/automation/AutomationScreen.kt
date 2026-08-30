package ir.parscode.app.ui.screens.automation

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.parscode.app.ui.components.GlowCard
import ir.parscode.app.ui.theme.*
import ir.parscode.app.util.DateUtils
import ir.parscode.app.worker.AutomationRun
import ir.parscode.app.worker.AutomationTaskDef
import ir.parscode.app.worker.AutomationTasks
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AutomationScreen(viewModel: AutomationViewModel = viewModel(factory = automationViewModelFactory())) {
    val tasks by viewModel.tasks.collectAsState()
    val runs by viewModel.runs.collectAsState()
    val pending by viewModel.pendingCount.collectAsState()
    var expandedKey by remember { mutableStateOf<String?>(null) }

    val todayRuns = remember(runs) { runs.filter { it.success && isToday(it.epochMillis) } }
    val lastRunLabel = remember(runs) { runs.maxByOrNull { it.epochMillis }?.let { formatHm(it.epochMillis) } ?: "—" }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PcBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconCircle(Icons.Filled.Settings, size = 34.dp)
                Spacer(Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text("فعالیت‌های خودکار", style = Typography.titleLarge, color = PcGold)
                    Text("مدیریت وظایف پس‌زمینه و خودکار برنامه", style = Typography.bodySmall, color = PcTextSecondary)
                }
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(PcSurface)
                        .border(1.dp, PcBorder, RoundedCornerShape(20.dp))
                        .clickable { viewModel.refreshAll() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text("به‌روزرسانی", color = PcTextPrimary, style = Typography.bodyMedium)
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Filled.Refresh, contentDescription = null, tint = PcGold, modifier = Modifier.size(16.dp))
                }
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("کل وظایف فعال", DateUtils.toPersianDigits(tasks.count { it.enabled }), "وظیفه", Icons.Filled.Settings, Modifier.weight(1f))
                StatCard("موفق امروز", DateUtils.toPersianDigits(todayRuns.size), "اجرا", Icons.Filled.CheckCircle, Modifier.weight(1f))
                StatCard("در انتظار", DateUtils.toPersianDigits(pending), "وظیفه", Icons.Filled.Schedule, Modifier.weight(1f))
                StatCard("آخرین اجرا", lastRunLabel, "امروز", Icons.Filled.CalendarMonth, Modifier.weight(1f))
            }
        }

        item { Text("وظایف خودکار", style = Typography.titleMedium, color = PcTextPrimary) }

        items(tasks, key = { it.def.key }) { uiTask ->
            TaskRow(
                uiTask = uiTask,
                expanded = expandedKey == uiTask.def.key,
                onToggleExpand = { expandedKey = if (expandedKey == uiTask.def.key) null else uiTask.def.key },
                onRunNow = { viewModel.runOnce(uiTask.def.key) },
                onToggleEnabled = { viewModel.toggle(uiTask.def, it) },
            )
        }

        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("آخرین اجراها", style = Typography.titleMedium, color = PcTextPrimary)
                Icon(Icons.Filled.Schedule, contentDescription = null, tint = PcTextSecondary, modifier = Modifier.size(18.dp))
            }
        }

        item {
            GlowCard(modifier = Modifier.fillMaxWidth()) {
                if (runs.isEmpty()) {
                    Text("هنوز اجرایی ثبت نشده است", color = PcTextSecondary, style = Typography.bodySmall)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        runs.take(6).forEach { run -> RunRow(run) }
                    }
                }
            }
        }

        item {
            GlowCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconCircle(Icons.Filled.Shield, size = 40.dp)
                    Spacer(Modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("تضمین امنیت و پایداری", style = Typography.titleMedium, color = PcGold)
                        Text(
                            "تمام فعالیت‌های خودکار در پس‌زمینه و با حداقل مصرف باتری اجرا می‌شوند. داده‌های شما امن و رمزگذاری‌شده‌اند.",
                            style = Typography.bodySmall, color = PcTextSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(
    uiTask: AutomationUiTask,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onRunNow: () -> Unit,
    onToggleEnabled: (Boolean) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    val def = uiTask.def

    GlowCard(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = uiTask.enabled,
                    onCheckedChange = onToggleEnabled,
                    colors = SwitchDefaults.colors(checkedThumbColor = PcGold, checkedTrackColor = PcGoldDim),
                )
                Spacer(Modifier.width(10.dp))
                IconCircle(def.icon, size = 40.dp)
                Spacer(Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(def.title, style = Typography.bodyLarge, color = PcTextPrimary, fontWeight = FontWeight.SemiBold)
                    Text(def.frequencyLabel, style = Typography.bodySmall, color = PcTextSecondary)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(uiTask.enabled)
                Spacer(Modifier.width(4.dp))
                Box {
                    Icon(Icons.Filled.MoreVert, contentDescription = "گزینه‌ها", tint = PcTextSecondary,
                        modifier = Modifier.clickable { showMenu = true })
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text("اجرای فوری") }, onClick = { showMenu = false; onRunNow() })
                    }
                }
                Icon(
                    if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.ChevronLeft,
                    contentDescription = null, tint = PcTextSecondary,
                    modifier = Modifier.clickable { onToggleExpand() },
                )
            }
        }

        if (expanded) {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = PcBorder, thickness = 1.dp)
            Spacer(Modifier.height(8.dp))
            Text(def.description, style = Typography.bodySmall, color = PcTextSecondary)
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, unit: String, icon: ImageVector, modifier: Modifier = Modifier) {
    GlowCard(modifier = modifier, contentPadding = PaddingValues(12.dp)) {
        Text(label, style = Typography.bodySmall, color = PcTextSecondary, maxLines = 1)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            IconCircle(icon, size = 26.dp)
            Column(horizontalAlignment = Alignment.End) {
                Text(value, style = Typography.titleLarge, color = PcGold)
                Text(unit, style = Typography.bodySmall, color = PcTextSecondary)
            }
        }
    }
}

@Composable
private fun IconCircle(icon: ImageVector, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(PcSurfaceRaised)
            .border(1.dp, PcBorderBright, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = PcGold, modifier = Modifier.size(size * 0.5f))
    }
}

@Composable
private fun StatusBadge(active: Boolean) {
    val color = if (active) PcSuccess else PcTextDisabled
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(if (active) "فعال" else "غیرفعال", style = Typography.bodySmall, color = color)
        Spacer(Modifier.width(4.dp))
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
    }
}

@Composable
private fun RunRow(run: AutomationRun) {
    val def = AutomationTasks.byKey(run.taskKey)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (run.success) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                contentDescription = null, tint = if (run.success) PcSuccess else PcDanger,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(if (run.success) "موفق" else "ناموفق", style = Typography.bodySmall, color = if (run.success) PcSuccess else PcDanger)
        }
        Text(formatHm(run.epochMillis), style = Typography.bodySmall, color = PcTextSecondary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(def.title, style = Typography.bodyMedium, color = PcTextPrimary)
            Spacer(Modifier.width(6.dp))
            Icon(def.icon, contentDescription = null, tint = PcGold, modifier = Modifier.size(16.dp))
        }
    }
}

private fun formatHm(epochMillis: Long): String =
    DateUtils.toPersianDigits(SimpleDateFormat("HH:mm", Locale.US).format(Date(epochMillis)))

private fun isToday(epochMillis: Long): Boolean {
    val a = Calendar.getInstance().apply { timeInMillis = epochMillis }
    val b = Calendar.getInstance()
    return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}
