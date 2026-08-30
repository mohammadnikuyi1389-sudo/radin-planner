package ir.parscode.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider

private val PcDarkColorScheme = darkColorScheme(
    primary = PcGold,
    onPrimary = PcBackground,
    secondary = PcGoldMuted,
    onSecondary = PcBackground,
    background = PcBackground,
    onBackground = PcTextPrimary,
    surface = PcSurface,
    onSurface = PcTextPrimary,
    surfaceVariant = PcSurfaceRaised,
    onSurfaceVariant = PcTextSecondary,
    outline = PcBorder,
    error = PcDanger,
    onError = PcTextPrimary,
)

/**
 * PARS CODE is a single-theme (always dark + gold), single-language (fa-IR,
 * RTL) app by design - the reference screens and the product brief both
 * specify this explicitly, so there is no light-mode branch here.
 */
@Composable
fun ParsCodeTheme(content: @Composable () -> Unit) {
    // isSystemInDarkTheme() intentionally unused for branching - kept as a
    // no-op read so a future light-theme toggle has an obvious hook point.
    isSystemInDarkTheme()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = PcDarkColorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content,
        )
    }
}
