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
        CoroutineScope(Dispatchers.IO).launch {
            val enabledKeys = AutomationTasks.ALL
                .filter { ServiceLocator.automationStore.enabledFlow(it.key).first() }
                .map { it.key }
                .toSet()
            AutomationScheduler.rescheduleEnabled(this@ParsCodeApplication, enabledKeys)
        }
    }
}
