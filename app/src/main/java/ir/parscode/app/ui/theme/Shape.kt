package ir.parscode.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

// Extra radii used by bespoke components (pill buttons, rings) beyond
// the Material Shapes scale above.
val PcRadiusPill = RoundedCornerShape(50)
val PcRadiusCard = RoundedCornerShape(18.dp)
