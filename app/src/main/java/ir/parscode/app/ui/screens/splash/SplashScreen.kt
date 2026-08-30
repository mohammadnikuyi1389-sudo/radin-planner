package ir.parscode.app.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.parscode.app.ui.components.ParsCodeLogo
import ir.parscode.app.ui.theme.PcBackground
import ir.parscode.app.ui.theme.PcGold
import ir.parscode.app.ui.theme.PcTextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1200)
        onFinished()
    }
    Box(
        modifier = Modifier.fillMaxSize().background(PcBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ParsCodeLogo(iconSize = 56.dp, showWordmark = false)
            Spacer(modifier = Modifier.height(16.dp))
            Text("PARS CODE", color = PcGold, fontSize = 26.sp, letterSpacing = 4.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text("مرکز فرمان شخصی شما", color = PcTextSecondary, fontSize = 13.sp)
        }
    }
}
