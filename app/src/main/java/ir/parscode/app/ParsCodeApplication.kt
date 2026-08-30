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

        CoroutineScope(Dispatchers.IO).launch {
            val enabledKeys = AutomationTasks.ALL
                .filter { ServiceLocator.automationStore.enabledFlow(it.key).first() }
                .map { it.key }
                .toSet()
            AutomationScheduler.rescheduleEnabled(this@ParsCodeApplication, enabledKeys)
        }
    }
}
