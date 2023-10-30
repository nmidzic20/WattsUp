package hr.foi.air.wattsup.pages

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.* // ktlint-disable no-wildcard-imports
import androidx.compose.material3.* // ktlint-disable no-wildcard-imports
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.ui.component.ModeButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingPage(onChargerModeClick: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // SpinningLogo()

                    BoxWithConstraints(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .width(200.dp),
                        // .background(color = Color.Blue),
                    ) {
                        val boxWidth = this.maxWidth

                        Box(
                            modifier = Modifier
                                .width(boxWidth - 150.dp)
                                // .background(Color.Red)
                                .align(Alignment.CenterStart),
                        ) {
                            SpinningLogo()
                        }

                        Row {
                            // Spacer(modifier = Modifier.width(100.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .width(boxWidth),
                                // .zIndex(-2f)
                                // .background(Color.Yellow),
                            ) {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    modifier = Modifier.padding(start = 8.dp), // Add padding to the text
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = "Select app mode:",
                style = MaterialTheme.typography.titleLarge,
            )

            ModeButton(
                mode = "User mode",
                iconId = R.drawable.icon_user_mode,
                onChargerModeClick = { },
            )

            ModeButton(
                mode = "Charger mode",
                iconId = R.drawable.icon_charger_mode,
                onChargerModeClick,
            )
        }
    }
}

@Composable
fun SpinningLogo() {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = R.drawable.icon_yellow).apply(block = {
                size(150, 150)
            }).build(),
            imageLoader = imageLoader,
        ),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview
@Composable
fun LandingPagePreview() {
    LandingPage {}
}
