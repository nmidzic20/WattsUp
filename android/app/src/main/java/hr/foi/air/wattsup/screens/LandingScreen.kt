package hr.foi.air.wattsup.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.ui.component.CircleButton
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.ui.component.TopAppBarLogoTitle

@Composable
fun LandingScreen(onChargerModeClick: () -> Unit, onUserModeClick: () -> Unit) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        LandscapeLayout(onChargerModeClick, onUserModeClick)
    } else {
        PortraitLayout(onChargerModeClick, onUserModeClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LandscapeLayout(onChargerModeClick: () -> Unit, onUserModeClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar({ TopAppBarLogoTitle() }, {})
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(it),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ModeSelection(onChargerModeClick, onUserModeClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortraitLayout(onChargerModeClick: () -> Unit, onUserModeClick: () -> Unit) {
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
            verticalArrangement = Arrangement.Center,
        ) {
            ModeSelection(onChargerModeClick, onUserModeClick)
        }
    }
}

@Composable
fun ModeSelection(onChargerModeClick: () -> Unit, onUserModeClick: () -> Unit) {
    CircleButton(
        mode = "User mode",
        iconId = R.drawable.icon_user_mode,
        onClick = onUserModeClick,
        color = null,
        modifier = Modifier.size(220.dp)
            .padding(16.dp),
    )

    Spacer(modifier = Modifier.size(32.dp))

    CircleButton(
        mode = "Charger mode",
        iconId = R.drawable.icon_charger_mode,
        onClick = onChargerModeClick,
        color = null,
        modifier = Modifier.size(220.dp)
            .padding(16.dp),
    )
}

@Preview
@Composable
fun LandingScreenPreview() {
    LandingScreen({ }, { })
}
