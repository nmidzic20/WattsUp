package hr.foi.air.wattsup.ui.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.ui.theme.colorSilver
import hr.foi.air.wattsup.ui.theme.colorTertiary

@Composable
fun GradientImage(
    iconResource: Int,
    backgroundColor: Color,
    fillColor: Color,
    fillPercentage: Float,
    height: Int,
    modifier: Modifier = Modifier,
) {
    val y = (1f - fillPercentage) * height * 2.8f
    val heightDp = height.dp

    val gradient = Brush.linearGradient(
        colors = listOf(
            backgroundColor,
            fillColor,
        ),
        start = Offset(0f, y - 0.1f),
        end = Offset(0f, y),
    )
    Image(
        modifier = modifier
            .height(heightDp)
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        gradient,
                        blendMode = BlendMode.SrcAtop,
                    )
                }
            },
        imageVector = ImageVector.vectorResource(id = iconResource),
        contentDescription = null,
        contentScale = ContentScale.Fit,

    )

    Log.i("VIEWMODELGRADIENTCOMPOSE", fillPercentage.toString())
}

@Preview()
@Composable
fun PreviewGradientImage() {
    GradientImage(
        R.drawable.icon_electric_car,
        colorSilver,
        colorTertiary,
        1f,
        150,
        Modifier.fillMaxWidth(),
    )
}
