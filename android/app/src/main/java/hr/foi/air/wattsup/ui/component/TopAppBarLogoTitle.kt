package hr.foi.air.wattsup.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R

@Composable
fun TopAppBarLogoTitle() {
    BoxWithConstraints(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .width(200.dp),
    ) {
        val boxWidth = this.maxWidth

        Box(
            modifier = Modifier
                .width(boxWidth - 150.dp)
                .align(Alignment.CenterStart),
        ) {
            SpinningLogo()
        }

        Row {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(boxWidth),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_text_white),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(30.dp)
                        .padding(start = 40.dp),
                )
            }
        }
    }
}
