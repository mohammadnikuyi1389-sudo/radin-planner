package ir.parscode.app.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.parscode.app.data.local.entity.GoalEntity
import ir.parscode.app.ui.components.GlowCard
import ir.parscode.app.ui.components.GoldButton
import ir.parscode.app.ui.theme.*
import ir.parscode.app.util.DateUtils

@Composable
fun GoalsScreen(viewModel: GoalsViewModel = viewModel(factory = goalsViewModelFactory())) {
    val goals by viewModel.goals.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<GoalEntity?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(PcBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Text("اهداف من", style = Typography.headlineMedium, color = PcGold) }
        item { GoldButton(text = "افزودن هدف جدید", onClick = { showAdd = true }) }
        if (goals.isEmpty()) {
            item { GlowCard(modifier = Modifier.fillMaxWidth()) { Text("هنوز هدفی ثبت نشده.", color = PcTextSecondary) } }
        } else {
            items(goals, key = { it.id }) { g ->
                GlowCard(modifier = Modifier.fillMaxWidth()) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(g.title, style = Typography.titleMedium, color = if (g.isCompleted) PcGold else PcTextPrimary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${DateUtils.toPersianDigits(g.progressPercent)}٪", color = PcGoldMuted)
                            Icon(Icons.Filled.Edit, contentDescription = "ویرایش", tint = PcTextSecondary, modifier = Modifier.size(18.dp).clickable { editingGoal = g })
                        }
                    }
                    if (g.deadlineIso != null) {
                        val deadlineText = runCatching { DateUtils.formatJalaliLong(g.deadlineIso) }.getOrDefault(g.deadlineIso)
                        Text("مهلت: $deadlineText", style = Typography.bodySmall, color = PcTextSecondary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    var sliderPos by remember(g.id, g.progressPercent) { mutableStateOf(g.progressPercent.toFloat()) }
                    Slider(
                        value = sliderPos,
                        onValueChange = { sliderPos = it },
                        onValueChangeFinished = { viewModel.updateProgress(g, sliderPos.toInt()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = PcGold, activeTrackColor = PcGold),
                    )
                    var showConfirm by remember { mutableStateOf(false) }
                    TextButton(onClick = { showConfirm = true }) { Text("حذف", color = PcDanger) }
                    if (showConfirm) {
                        AlertDialog(
                            onDismissRequest = { showConfirm = false },
                            containerColor = PcSurface,
                            title = { Text("حذف «${g.title}»؟") },
                            confirmButton = { TextButton(onClick = { viewModel.delete(g); showConfirm = false }) { Text("حذف", color = PcDanger) } },
                            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("انصراف") } },
                        )
                    }
                }
            }
        }
    }

    if (showAdd) {
        GoalFormDialog(title = "افزودن هدف", confirmLabel = "افزودن", onDismiss = { showAdd = false }) { t, d ->
            viewModel.add(t, d); showAdd = false
        }
    }
    editingGoal?.let { g ->
        GoalFormDialog(
            title = "ویرایش هدف", confirmLabel = "ذخیره",
            initialTitle = g.title, initialDeadline = g.deadlineIso ?: "",
            onDismiss = { editingGoal = null },
        ) { t, d -> viewModel.edit(g, t, d); editingGoal = null }
    }
}

@Composable
private fun GoalFormDialog(
    title: String,
    confirmLabel: String,
    initialTitle: String = "",
    initialDeadline: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit,
) {
    var t by remember { mutableStateOf(initialTitle) }
    var d by remember { mutableStateOf(initialDeadline) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PcSurface,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(value = t, onValueChange = { t = it }, label = { Text("عنوان هدف") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = d, onValueChange = { d = it }, label = { Text("مهلت (اختیاری)") })
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(t, d.ifBlank { null }) }, enabled = t.isNotBlank()) { Text(confirmLabel, color = PcGold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
    )
}
