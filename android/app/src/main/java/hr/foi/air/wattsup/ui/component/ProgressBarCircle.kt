package hr.foi.air.wattsup.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class ProgressBarFill(_value: Float) {
    val value: Float
    val percentage: Float
    val progressSweepAngle: Float

    init {
        value = _value.coerceIn(0f, 10f)
        percentage = (value / 10)
        progressSweepAngle = 360f * (value / 10f)
    }
}

@Composable
fun ProgressBarCircle(progressBarFill: ProgressBarFill, fillColor: Color, modifier: Modifier = Modifier) {
    val colorCircle = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            drawCircle(
                color = colorCircle,
                style = Stroke(15.dp.toPx(), cap = StrokeCap.Round),
            )
            drawArc(
                color = fillColor,
                startAngle = -90f,
                progressBarFill.progressSweepAngle,
                useCenter = false,
                style = Stroke(15.dp.toPx(), cap = StrokeCap.Round),
            )
        }
    }
}

@Preview
@Composable
fun ProgressBarCirclePreview() {
    val progressBarFill = ProgressBarFill(7.5f)
    ProgressBarCircle(progressBarFill, Color.Yellow, Modifier.size(42.dp))
}
