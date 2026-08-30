package ir.parscode.app.util

import androidx.compose.ui.graphics.Color
import ir.parscode.app.ui.theme.PcDanger
import ir.parscode.app.ui.theme.PcGold
import ir.parscode.app.ui.theme.PcSuccess

/**
 * Single source of truth for "status" everywhere a percent exists (program
 * weeks, weekly goals). Status is always derived from the percent instead
 * of being stored separately, so it can never disagree with the number
 * shown next to it.
 */
object ProgressStatus {
    fun labelFa(percent: Int): String = when {
        percent >= 100 -> "تکمیل شده"
        percent > 0 -> "در حال پیشرفت"
        else -> "آغاز نشده"
    }

    fun color(percent: Int): Color = when {
        percent >= 100 -> PcSuccess
        percent > 0 -> PcGold
        else -> PcDanger
    }
}
