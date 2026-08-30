package ir.parscode.app

import android.app.Application
import ir.parscode.app.di.ServiceLocator
import ir.parscode.app.worker.AutomationScheduler
import ir.parscode.app.worker.AutomationTasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ParsCodeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)

        // Re-arm every background job the user left on, so schedules survive
        // process death and device reboot (WorkManager persists the queue,
        // but only for jobs that were enqueued at least once after boot).
        // Uses each task's saved/custom schedule (time or interval the user
        // may have changed on the automation screen), not just the built-in
        // default - otherwise a saved time change would silently revert to
        // the default every time the app restarts.
        CoroutineScope(Dispatchers.IO).launch {
            val enabledSchedules = AutomationTasks.ALL
                .filter { ServiceLocator.automationStore.enabledFlow(it.key).first() }
                .associate { it.key to ServiceLocator.automationStore.effectiveScheduleOnce(it) }
            AutomationScheduler.rescheduleEnabled(this@ParsCodeApplication, enabledSchedules)
        }
    }
}
