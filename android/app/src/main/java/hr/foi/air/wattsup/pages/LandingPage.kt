package hr.foi.air.wattsup.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.ui.component.ModeButton
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.ui.component.TopAppBarLogoTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingPage(onChargerModeClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar({ TopAppBarLogoTitle() }, {})
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
                onClick = { },
            )

            ModeButton(
                mode = "Charger mode",
                iconId = R.drawable.icon_charger_mode,
                onClick = onChargerModeClick,
            )
        }
    }
}

@Preview
@Composable
fun LandingPagePreview() {
    LandingPage {}
}
