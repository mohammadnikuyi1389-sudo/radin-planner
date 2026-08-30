package ir.parscode.app.worker

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.automationDataStore by preferencesDataStore(name = "automation")
private val LOG_KEY = stringPreferencesKey("automation_run_log")
private const val LOG_MAX = 20

/** One completed run of a background task, newest first. */
data class AutomationRun(val taskKey: String, val epochMillis: Long, val success: Boolean)

/**
 * Persists per-task enabled/disabled state and a rolling log of the last
 * runs, so the automation screen and the actual WorkManager scheduling stay
 * in sync across process restarts.
 */
class AutomationStore(private val context: Context) {

    fun enabledFlow(taskKey: String): Flow<Boolean> =
        context.automationDataStore.data.map { it[booleanPreferencesKey("enabled_$taskKey")] ?: true }

    suspend fun setEnabled(taskKey: String, enabled: Boolean) {
        context.automationDataStore.edit { it[booleanPreferencesKey("enabled_$taskKey")] = enabled }
    }

    val logFlow: Flow<List<AutomationRun>> = context.automationDataStore.data.map { prefs ->
        val raw = prefs[LOG_KEY] ?: return@map emptyList()
        raw.split("|").filter { it.isNotBlank() }.mapNotNull { entry ->
            val parts = entry.split(":")
            if (parts.size != 3) return@mapNotNull null
            val key = parts[0]
            val time = parts[1].toLongOrNull() ?: return@mapNotNull null
            AutomationRun(key, time, parts[2] == "1")
        }
    }

    suspend fun logRun(taskKey: String, success: Boolean) {
        context.automationDataStore.edit { prefs ->
            val existing = (prefs[LOG_KEY] ?: "").split("|").filter { it.isNotBlank() }
            val entry = "$taskKey:${System.currentTimeMillis()}:${if (success) "1" else "0"}"
            prefs[LOG_KEY] = (listOf(entry) + existing).take(LOG_MAX).joinToString("|")
        }
    }
}
