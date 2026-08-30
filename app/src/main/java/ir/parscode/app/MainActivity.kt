package ir.parscode.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import ir.parscode.app.di.ServiceLocator
import ir.parscode.app.ui.navigation.ParsCodeNavGraph
import ir.parscode.app.ui.theme.ParsCodeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            // If the user denies it, fall back to keeping the in-app toggle off so the
            // switch on the settings screen doesn't silently lie about the real state.
            if (!granted) {
                lifecycleScope.launch {
                    ServiceLocator.settingsDataStore.setNotificationsEnabled(false)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermissionIfNeeded()
        setContent {
            ParsCodeTheme {
                ParsCodeNavGraph()
            }
        }
    }

    /**
     * Android 13+ (API 33+) requires POST_NOTIFICATIONS to be requested at runtime,
     * otherwise every reminder/automation notification is silently dropped by the
     * system even though the permission is declared in the manifest.
     */
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val alreadyGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!alreadyGranted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
