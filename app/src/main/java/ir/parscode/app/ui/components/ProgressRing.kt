package ir.parscode.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.parscode.app.ui.theme.PcBorder
import ir.parscode.app.ui.theme.PcGold
import ir.parscode.app.ui.theme.PcTextPrimary
import ir.parscode.app.ui.theme.Typography
import ir.parscode.app.util.DateUtils

/** Circular percentage ring, e.g. the "۷۸٪ پیشرفت امروز" dashboard gauge. */
@Composable
fun ProgressRing(
    percent: Int,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 96.dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 8.dp,
    ringColor: androidx.compose.ui.graphics.Color = PcGold,
    textStyle: androidx.compose.ui.text.TextStyle = Typography.titleMedium,
) {
    val clamped = percent.coerceIn(0, 100)
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            val inset = strokeWidth.toPx() / 2
            val arcSize = Size(size.toPx() - strokeWidth.toPx(), size.toPx() - strokeWidth.toPx())
            drawArc(
                color = PcBorder,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = arcSize,
                style = stroke,
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * (clamped / 100f),
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = arcSize,
                style = stroke,
            )
        }
        Text(
            text = "${DateUtils.toPersianDigits(clamped)}٪",
            style = textStyle,
            color = PcTextPrimary,
            textAlign = TextAlign.Center,
        )
    }
}
