package ir.parscode.app.ui.screens.automation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ir.parscode.app.di.ServiceLocator
import ir.parscode.app.worker.AutomationRun
import ir.parscode.app.worker.AutomationScheduler
import ir.parscode.app.worker.AutomationTaskDef
import ir.parscode.app.worker.AutomationTasks
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AutomationUiTask(val def: AutomationTaskDef, val enabled: Boolean)

class AutomationViewModel : ViewModel() {
    private val store = ServiceLocator.automationStore
    private val context = ServiceLocator.appContext

    val tasks: StateFlow<List<AutomationUiTask>> =
        combine(AutomationTasks.ALL.map { def -> store.enabledFlow(def.key).map { def to it } }) { pairs ->
            pairs.map { (def, enabled) -> AutomationUiTask(def, enabled) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AutomationTasks.ALL.map { AutomationUiTask(it, true) })

    val runs: StateFlow<List<AutomationRun>> =
        store.logFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingCount: StateFlow<Int> =
        AutomationScheduler.pendingCountFlow(context).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun toggle(def: AutomationTaskDef, enabled: Boolean) {
        viewModelScope.launch {
            store.setEnabled(def.key, enabled)
            AutomationScheduler.apply(context, def, enabled)
        }
    }

    fun runOnce(key: String) = AutomationScheduler.runNow(context, listOf(key))

    fun refreshAll() = AutomationScheduler.runNow(context, tasks.value.filter { it.enabled }.map { it.def.key })
}

fun automationViewModelFactory() = viewModelFactory { initializer { AutomationViewModel() } }
