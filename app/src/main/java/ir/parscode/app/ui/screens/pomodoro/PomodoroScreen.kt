package ir.parscode.app.ui.screens.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.parscode.app.ui.components.GlowCard
import ir.parscode.app.ui.components.GoldButton
import ir.parscode.app.ui.components.ProgressRing
import ir.parscode.app.ui.theme.*
import ir.parscode.app.util.DateUtils

@Composable
fun PomodoroScreen(viewModel: PomodoroViewModel = viewModel(factory = pomodoroViewModelFactory())) {
    val s by viewModel.state.collectAsState()
    val doneToday by viewModel.historyCount.collectAsState()
    val percent = if (s.totalSeconds == 0) 0 else ((s.totalSeconds - s.remainingSeconds) * 100 / s.totalSeconds)
    val mm = s.remainingSeconds / 60
    val ss = s.remainingSeconds % 60

    Column(
        modifier = Modifier.fillMaxSize().background(PcBackground).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("تمرکز پومودورو", style = Typography.headlineMedium, color = PcGold)

        Box(contentAlignment = Alignment.Center) {
            ProgressRing(percent = percent, size = 180.dp, strokeWidth = 10.dp)
            Text(
                "${DateUtils.toPersianDigits(mm.toString().padStart(2, '0'))}:${DateUtils.toPersianDigits(ss.toString().padStart(2, '0'))}",
                style = Typography.headlineMedium,
                color = PcTextPrimary,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(25 to "FOCUS", 5 to "SHORT_BREAK", 15 to "LONG_BREAK").forEach { (min, kind) ->
                val label = when (kind) { "FOCUS" -> "تمرکز ${DateUtils.toPersianDigits(min)} دقیقه"; "SHORT_BREAK" -> "استراحت ${DateUtils.toPersianDigits(min)} دقیقه"; else -> "استراحت بلند ${DateUtils.toPersianDigits(min)} دقیقه" }
                androidx.compose.material3.AssistChip(
                    onClick = { viewModel.selectDuration(min, kind) },
                    label = { Text(label, style = Typography.bodySmall) },
                )
            }
        }

        GoldButton(text = if (s.isRunning) "توقف" else "شروع جلسه", onClick = { if (s.isRunning) viewModel.pause() else viewModel.start() }, showPlusIcon = false)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            androidx.compose.material3.TextButton(onClick = { viewModel.reset() }) { Text("بازنشانی", color = PcTextSecondary) }
        }

        GlowCard(modifier = Modifier.fillMaxWidth()) {
            Text("جلسات کامل امروز: ${DateUtils.toPersianDigits(doneToday)}", color = PcGold)
        }
    }
}
