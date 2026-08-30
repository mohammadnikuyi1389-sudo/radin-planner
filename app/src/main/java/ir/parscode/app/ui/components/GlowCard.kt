package ir.parscode.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import ir.parscode.app.ui.theme.PcBorder
import ir.parscode.app.ui.theme.PcSurface
import ir.parscode.app.ui.theme.PcSurfaceRaised

/**
 * The single recurring surface used across every reference screen: a
 * near-black card, a hairline gold-tinted border, and a very subtle
 * top-to-bottom gradient standing in for the images' soft inner glow -
 * kept restrained since the brief calls for "بسیار کنترل‌شده" glow use.
 */
@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(listOf(PcSurfaceRaised, PcSurface)))
            .border(1.dp, PcBorder, RoundedCornerShape(18.dp))
            .padding(contentPadding),
        content = content,
    )
}
