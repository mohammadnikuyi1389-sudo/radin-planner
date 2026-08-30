package ir.parscode.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.parscode.app.ui.navigation.NavRoutes
import ir.parscode.app.ui.theme.*
import ir.parscode.app.util.DateUtils

@Composable
fun AppTopBar(
    userName: String,
    userRole: String,
    notificationCount: Int,
    onNavigate: (String) -> Unit = {},
) {
    var showNotifications by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.background(PcBackground)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onNavigate(NavRoutes.PROFILE) }) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(PcSurfaceRaised), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = PcGold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(userName, style = Typography.labelLarge, color = PcTextPrimary)
                    Text(userRole, style = Typography.bodySmall, color = PcTextSecondary)
                }
            }

            ParsCodeLogo(iconSize = 20.dp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clickable { showNotifications = true }) {
                    Icon(Icons.Filled.Notifications, contentDescription = "اعلان‌ها", tint = PcTextSecondary)
                    if (notificationCount > 0) {
                        Box(
                            modifier = Modifier.size(14.dp).clip(CircleShape).background(PcGold).align(Alignment.TopEnd),
                            contentAlignment = Alignment.Center,
                        ) { Text(DateUtils.toPersianDigits(notificationCount), fontSize = 9.sp, color = PcBackground) }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Filled.Menu, contentDescription = "منو", tint = PcTextSecondary, modifier = Modifier.clickable { showMenu = true })
            }
        }
        HorizontalDivider(color = PcBorder, thickness = 1.dp)
    }

    if (showNotifications) {
        AlertDialog(
            onDismissRequest = { showNotifications = false },
            containerColor = PcSurface,
            title = { Text("اعلان‌ها") },
            text = {
                Column {
                    Text("• یادآور: وظایف امروز را بررسی کنید", color = PcTextPrimary, style = Typography.bodyMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• رکورد متوالی عادت‌های شما در حال رشد است", color = PcTextPrimary, style = Typography.bodyMedium)
                }
            },
            confirmButton = { TextButton(onClick = { showNotifications = false }) { Text("بستن", color = PcGold) } },
        )
    }

    if (showMenu) {
        val items = listOf(
            "داشبورد" to NavRoutes.DASHBOARD, "برنامه روزانه" to NavRoutes.DAILY_PLAN,
            "نمای هفتگی" to NavRoutes.WEEKLY, "عادت‌ها" to NavRoutes.HABITS,
            "اهداف" to NavRoutes.GOALS, "برنامه ۱۲ هفته‌ای" to NavRoutes.TWELVE_WEEK,
            "پومودورو" to NavRoutes.POMODORO, "کتابخانه" to NavRoutes.LIBRARY,
            "مالی" to NavRoutes.FINANCE, "پروفایل" to NavRoutes.PROFILE, "تنظیمات" to NavRoutes.SETTINGS,
        )
        AlertDialog(
            onDismissRequest = { showMenu = false },
            containerColor = PcSurface,
            title = { Text("منو") },
            text = {
                Column {
                    items.forEach { (label, route) ->
                        Text(
                            label,
                            color = PcTextPrimary,
                            style = Typography.bodyLarge,
                            modifier = Modifier.fillMaxWidth().clickable { onNavigate(route); showMenu = false }.padding(vertical = 8.dp),
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showMenu = false }) { Text("بستن", color = PcGold) } },
        )
    }
}
