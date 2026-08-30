package ir.parscode.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import ir.parscode.app.ui.theme.PcBackground
import ir.parscode.app.ui.theme.PcGold
import ir.parscode.app.ui.theme.PcGoldBright
import ir.parscode.app.ui.theme.Typography

/** Full-width gold gradient pill button, used for every primary "افزودن ..." action. */
@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showPlusIcon: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(Brush.horizontalGradient(listOf(PcGoldBright, PcGold)))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = Typography.labelLarge, color = PcBackground)
        if (showPlusIcon) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = PcBackground,
                modifier = Modifier.padding(start = 6.dp),
            )
        }
    }
}
