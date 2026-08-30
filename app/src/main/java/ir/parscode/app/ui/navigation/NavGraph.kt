package ir.parscode.app.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ir.parscode.app.ui.components.AppTopBar
import ir.parscode.app.ui.components.BottomNavBar
import ir.parscode.app.ui.screens.automation.AutomationScreen
import ir.parscode.app.ui.screens.dailyplan.DailyPlanScreen
import ir.parscode.app.ui.screens.dashboard.DashboardScreen
import ir.parscode.app.ui.screens.finance.FinanceScreen
import ir.parscode.app.ui.screens.goals.GoalsScreen
import ir.parscode.app.ui.screens.habits.HabitsScreen
import ir.parscode.app.ui.screens.library.LibraryScreen
import ir.parscode.app.ui.screens.pomodoro.PomodoroScreen
import ir.parscode.app.ui.screens.profile.ProfileScreen
import ir.parscode.app.ui.screens.settings.SettingsScreen
import ir.parscode.app.ui.screens.splash.SplashScreen
import ir.parscode.app.ui.screens.twelveweek.TwelveWeekScreen
import ir.parscode.app.ui.screens.weekly.WeeklyScreen

private val CHROME_ROUTES = setOf(
    NavRoutes.DASHBOARD, NavRoutes.DAILY_PLAN, NavRoutes.HABITS, NavRoutes.FINANCE, NavRoutes.SETTINGS,
    NavRoutes.GOALS, NavRoutes.TWELVE_WEEK, NavRoutes.WEEKLY, NavRoutes.POMODORO, NavRoutes.LIBRARY, NavRoutes.PROFILE,
    NavRoutes.AUTOMATION,
)
private val BOTTOM_TABS = setOf(NavRoutes.DASHBOARD, NavRoutes.DAILY_PLAN, NavRoutes.HABITS, NavRoutes.FINANCE, NavRoutes.SETTINGS)
// Sub-screens reached from a tab keep that tab highlighted in the bottom bar.
private val TAB_FOR_ROUTE = mapOf(NavRoutes.AUTOMATION to NavRoutes.SETTINGS)

@Composable
fun ParsCodeNavGraph(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: NavRoutes.SPLASH
    val showChrome = currentRoute in CHROME_ROUTES
    val navigate: (String) -> Unit = { route ->
        navController.navigate(route) { launchSingleTop = true }
    }

    Scaffold(
        topBar = {
            if (showChrome) AppTopBar(userName = "محمد نیکویی", userRole = "کاربر حرفه‌ای", notificationCount = 3, onNavigate = navigate)
        },
        bottomBar = {
            val highlightRoute = TAB_FOR_ROUTE[currentRoute] ?: currentRoute
            if (showChrome && highlightRoute in BOTTOM_TABS) BottomNavBar(currentRoute = highlightRoute) { route ->
                navController.navigate(route) {
                    popUpTo(NavRoutes.DASHBOARD) { inclusive = false }
                    launchSingleTop = true
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            NavHost(navController = navController, startDestination = NavRoutes.SPLASH) {
                composable(NavRoutes.SPLASH) {
                    SplashScreen(onFinished = {
                        navController.navigate(NavRoutes.DASHBOARD) { popUpTo(NavRoutes.SPLASH) { inclusive = true } }
                    })
                }
                composable(NavRoutes.DASHBOARD) { DashboardScreen(onNavigate = navigate) }
                composable(NavRoutes.HABITS) { HabitsScreen() }
                composable(NavRoutes.DAILY_PLAN) { DailyPlanScreen() }
                composable(NavRoutes.FINANCE) { FinanceScreen() }
                composable(NavRoutes.SETTINGS) { SettingsScreen(onNavigate = navigate) }
                composable(NavRoutes.AUTOMATION) { AutomationScreen() }
                composable(NavRoutes.GOALS) { GoalsScreen() }
                composable(NavRoutes.TWELVE_WEEK) { TwelveWeekScreen(onNavigate = navigate) }
                composable(NavRoutes.WEEKLY) { WeeklyScreen() }
                composable(NavRoutes.POMODORO) { PomodoroScreen() }
                composable(NavRoutes.LIBRARY) { LibraryScreen() }
                composable(NavRoutes.PROFILE) { ProfileScreen() }
            }
        }
    }
}
