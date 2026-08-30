package ir.parscode.app.worker

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.ui.graphics.vector.ImageVector

/** How often a background task should run. */
sealed class AutomationSchedule {
    data class EveryMinutes(val minutes: Long) : AutomationSchedule()
    data class DailyAt(val hour: Int, val minute: Int) : AutomationSchedule()
}

data class AutomationTaskDef(
    val key: String,
    val title: String,
    val description: String,
    val frequencyLabel: String,
    val icon: ImageVector,
    val schedule: AutomationSchedule,
)

/** The six background jobs the app actually schedules through WorkManager. */
object AutomationTasks {
    val ALL = listOf(
        AutomationTaskDef(
            key = "reminder",
            title = "یادآوری وظایف",
            description = "ارسال اعلان یادآوری برای وظایف برنامه‌ریزی‌شده",
            frequencyLabel = "هر ۱۵ دقیقه",
            icon = Icons.Filled.Notifications,
            schedule = AutomationSchedule.EveryMinutes(15),
        ),
        AutomationTaskDef(
            key = "backup",
            title = "پشتیبان‌گیری خودکار",
            description = "تهیه نسخه پشتیبان از داده‌های برنامه",
            frequencyLabel = "هر روز / ۰۲:۰۰",
            icon = Icons.Filled.CloudUpload,
            schedule = AutomationSchedule.DailyAt(2, 0),
        ),
        AutomationTaskDef(
            key = "sync",
            title = "همگام‌سازی داده‌ها",
            description = "همگام‌سازی داده‌ها بین دستگاه‌ها",
            frequencyLabel = "هر ۳۰ دقیقه",
            icon = Icons.Filled.Sync,
            schedule = AutomationSchedule.EveryMinutes(30),
        ),
        AutomationTaskDef(
            key = "report",
            title = "گزارش روزانه",
            description = "ایجاد گزارش روزانه از فعالیت‌ها و پیشرفت‌ها",
            frequencyLabel = "هر روز / ۲۱:۰۰",
            icon = Icons.Filled.Description,
            schedule = AutomationSchedule.DailyAt(21, 0),
        ),
        AutomationTaskDef(
            key = "metrics",
            title = "به‌روزرسانی شاخص‌ها",
            description = "به‌روزرسانی آمار و محاسبه شاخص‌ها",
            frequencyLabel = "هر ۶۰ دقیقه",
            icon = Icons.Filled.BarChart,
            schedule = AutomationSchedule.EveryMinutes(60),
        ),
        AutomationTaskDef(
            key = "cache",
            title = "پاکسازی کش",
            description = "پاکسازی فایل‌های موقت و کش برنامه",
            frequencyLabel = "هر روز / ۰۳:۰۰",
            icon = Icons.Filled.Storage,
            schedule = AutomationSchedule.DailyAt(3, 0),
        ),
    )

    fun byKey(key: String) = ALL.first { it.key == key }
}
