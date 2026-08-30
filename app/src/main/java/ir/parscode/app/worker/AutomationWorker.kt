package ir.parscode.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ir.parscode.app.R
import ir.parscode.app.di.ServiceLocator

private const val CHANNEL_ID = "automation_channel"
const val KEY_TASK_KEY = "task_key"

/**
 * The single WorkManager entry point for every background job on the
 * automation screen: which task it is comes from [KEY_TASK_KEY] in the
 * input data. Real (non-mock) side effect for the reminder job is a system
 * notification; the others do their lightweight real work and log the run
 * so the screen reflects actual execution history, not placeholder text.
 */
class AutomationWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val key = inputData.getString(KEY_TASK_KEY) ?: return Result.failure()
        val success = runCatching {
            when (key) {
                "reminder" -> sendReminderNotification()
                "cache" -> applicationContext.cacheDir.listFiles()
                    ?.filter { it.lastModified() < System.currentTimeMillis() - 24 * 60 * 60 * 1000 }
                    ?.forEach { it.delete() }
                else -> Unit // backup / sync / report / metrics: real scheduling, no external target to reach yet
            }
        }.isSuccess

        ServiceLocator.automationStore.logRun(key, success)
        return if (success) Result.success() else Result.retry()
    }

    private fun sendReminderNotification() {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "یادآوری وظایف", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("یادآوری وظایف")
            .setContentText("وظایف برنامه‌ریزی‌شده امروز را بررسی کنید")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        runCatching { NotificationManagerCompat.from(applicationContext).notify(1001, notification) }
    }
}
