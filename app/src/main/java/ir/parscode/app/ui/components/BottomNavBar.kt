package ir.parscode.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ir.parscode.app.ui.navigation.NavRoutes
import ir.parscode.app.ui.theme.PcBackground
import ir.parscode.app.ui.theme.PcBorder
import ir.parscode.app.ui.theme.PcGold
import ir.parscode.app.ui.theme.PcTextSecondary
import ir.parscode.app.ui.theme.Typography

private data class NavItem(val route: String, val label: String, val icon: ImageVector)

// Order = start-to-end in RTL, so first item renders rightmost (matches reference: داشبورد rightmost).
private val ITEMS = listOf(
    NavItem(NavRoutes.DASHBOARD, "داشبورد", Icons.Filled.Home),
    NavItem(NavRoutes.DAILY_PLAN, "برنامه", Icons.Filled.CalendarMonth),
    NavItem(NavRoutes.HABITS, "عادت‌ها", Icons.Filled.CheckCircle),
    NavItem(NavRoutes.FINANCE, "مالی", Icons.Filled.BarChart),
    NavItem(NavRoutes.SETTINGS, "تنظیمات", Icons.Filled.Settings),
)

@Composable
fun BottomNavBar(currentRoute: String, onNavigate: (String) -> Unit) {
    Column(modifier = Modifier.background(PcBackground)) {
        HorizontalDivider(color = PcBorder, thickness = 1.dp)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ITEMS.forEach { item ->
                val selected = item.route == currentRoute
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onNavigate(item.route) }.padding(4.dp),
                ) {
                    Icon(item.icon, contentDescription = item.label, tint = if (selected) PcGold else PcTextSecondary)
                    Text(item.label, style = Typography.bodySmall, color = if (selected) PcGold else PcTextSecondary)
                }
            }
        }
    }
}
