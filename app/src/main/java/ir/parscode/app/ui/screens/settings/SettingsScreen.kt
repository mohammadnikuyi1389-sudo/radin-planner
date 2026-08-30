package ir.parscode.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.parscode.app.ui.navigation.NavRoutes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ir.parscode.app.di.ServiceLocator
import ir.parscode.app.ui.components.GlowCard
import ir.parscode.app.ui.theme.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private val store = ServiceLocator.settingsDataStore
    val notificationsEnabled: StateFlow<Boolean> = store.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    fun setNotifications(enabled: Boolean) { viewModelScope.launch { store.setNotificationsEnabled(enabled) } }
}
fun settingsViewModelFactory() = viewModelFactory { initializer { SettingsViewModel() } }

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = settingsViewModelFactory()),
    onNavigate: (String) -> Unit = {},
) {
    val notifOn by viewModel.notificationsEnabled.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(PcBackground).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("تنظیمات", style = Typography.headlineMedium, color = PcGold)

        GlowCard(modifier = Modifier.fillMaxWidth().clickable { onNavigate(NavRoutes.AUTOMATION) }) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = null, tint = PcTextSecondary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("فعالیت‌های خودکار", color = PcTextPrimary)
                        Text("مدیریت وظایف پس‌زمینه", style = Typography.bodySmall, color = PcTextSecondary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(Icons.Filled.Settings, contentDescription = null, tint = PcGold)
                }
            }
        }
        GlowCard(modifier = Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("اعلان‌ها", color = PcTextPrimary)
                Switch(
                    checked = notifOn,
                    onCheckedChange = { viewModel.setNotifications(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = PcGold, checkedTrackColor = PcGoldDim),
                )
            }
        }
        GlowCard(modifier = Modifier.fillMaxWidth()) {
            Text("زبان برنامه", color = PcTextPrimary)
            Text("فارسی", style = Typography.bodySmall, color = PcTextSecondary)
        }
        GlowCard(modifier = Modifier.fillMaxWidth()) {
            Text("تم برنامه", color = PcTextPrimary)
            Text("طلایی (پیش‌فرض)", style = Typography.bodySmall, color = PcTextSecondary)
        }
        GlowCard(modifier = Modifier.fillMaxWidth()) {
            Text("درباره برنامه", color = PcTextPrimary)
            Text("نسخه ۱.۰.۰", style = Typography.bodySmall, color = PcTextSecondary)
        }
    }
}
