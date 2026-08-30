package ir.parscode.app.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import java.util.concurrent.TimeUnit

/** Turns an [AutomationTaskDef] into real, unique WorkManager jobs. */
object AutomationScheduler {

    private fun inputData(key: String) = Data.Builder().putString(KEY_TASK_KEY, key).build()

    private fun initialDelayMillis(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute); set(Calendar.SECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    fun schedule(context: Context, task: AutomationTaskDef) {
        val builder = when (val s = task.schedule) {
            is AutomationSchedule.EveryMinutes ->
                PeriodicWorkRequestBuilder<AutomationWorker>(s.minutes, TimeUnit.MINUTES)
            is AutomationSchedule.DailyAt ->
                PeriodicWorkRequestBuilder<AutomationWorker>(1, TimeUnit.DAYS)
                    .setInitialDelay(initialDelayMillis(s.hour, s.minute), TimeUnit.MILLISECONDS)
        }
        val request = builder.setInputData(inputData(task.key)).build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(task.key, ExistingPeriodicWorkPolicy.UPDATE, request)
    }

    fun cancel(context: Context, taskKey: String) {
        WorkManager.getInstance(context).cancelUniqueWork(taskKey)
    }

    fun apply(context: Context, task: AutomationTaskDef, enabled: Boolean) {
        if (enabled) schedule(context, task) else cancel(context, task.key)
    }

    /** Re-registers every task the user has left enabled - call on app start. */
    fun rescheduleEnabled(context: Context, enabledKeys: Set<String>) {
        AutomationTasks.ALL.filter { it.key in enabledKeys }.forEach { schedule(context, it) }
    }

    /** Runs every enabled task once, immediately - backs the "به‌روزرسانی" button. */
    fun runNow(context: Context, taskKeys: List<String>) {
        val wm = WorkManager.getInstance(context)
        taskKeys.forEach { key ->
            wm.enqueueUniqueWork(
                "${key}_manual",
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<AutomationWorker>().setInputData(inputData(key)).build(),
            )
        }
    }

    /** True while a task's periodic job is enqueued/waiting or actively running. */
    fun pendingCountFlow(context: Context): Flow<Int> {
        val wm = WorkManager.getInstance(context)
        val flows = AutomationTasks.ALL.map { task -> wm.getWorkInfosForUniqueWorkFlow(task.key) }
        return combine(flows) { infoLists ->
            infoLists.count { infos -> infos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING } }
        }
    }
}
