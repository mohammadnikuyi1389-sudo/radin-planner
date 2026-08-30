package ir.parscode.app.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")
private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")

class SettingsDataStore(private val context: Context) {
    val notificationsEnabled = context.dataStore.data.map { it[NOTIFICATIONS_KEY] ?: true }
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFICATIONS_KEY] = enabled }
    }
}
