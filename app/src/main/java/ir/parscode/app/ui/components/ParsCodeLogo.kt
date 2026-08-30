package ir.parscode.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.parscode.app.ui.theme.PcGold

/**
 * Stand-in for the "PARS CODE" pillar emblem from the reference splash
 * screen: a classical-column glyph (closest built-in Material icon to the
 * reference artwork) over the gold wordmark, letter-spaced like the source.
 */
@Composable
fun ParsCodeLogo(
    modifier: Modifier = Modifier,
    iconSize: androidx.compose.ui.unit.Dp = 22.dp,
    showWordmark: Boolean = true,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Filled.AccountBalance,
            contentDescription = "PARS CODE",
            tint = PcGold,
            modifier = Modifier.size(iconSize),
        )
        if (showWordmark) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "PARS CODE",
                color = PcGold,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
            )
        }
    }
}
