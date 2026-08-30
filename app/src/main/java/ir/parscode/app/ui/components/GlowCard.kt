package ir.parscode.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import ir.parscode.app.ui.theme.PcBorder
import ir.parscode.app.ui.theme.PcSurface
import ir.parscode.app.ui.theme.PcSurfaceRaised

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
