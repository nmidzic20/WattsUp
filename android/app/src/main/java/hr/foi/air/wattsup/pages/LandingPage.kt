package hr.foi.air.wattsup.pages

import androidx.compose.foundation.layout.* // ktlint-disable no-wildcard-imports
import androidx.compose.material3.* // ktlint-disable no-wildcard-imports
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.ui.component.ModeButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingPage(onChargerModeClick: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
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

@Preview
@Composable
fun LandingPagePreview() {
    LandingPage({})
}
