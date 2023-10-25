package hr.foi.air.wattsup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import hr.foi.air.charging.ChargingModuleImpl
import hr.foi.air.core.AppMode
import hr.foi.air.user_mode.UserModuleImpl

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        UserModeSelectionScreen()
    }
}

@Composable
fun UserModeSelectionScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var appMode: AppMode by remember { mutableStateOf(ChargingModuleImpl()) }

        Button(
            onClick = {
                appMode = ChargingModuleImpl()
                // appMode.displayUI()
            },
            modifier = Modifier.padding(16.dp),
        ) {
            Text("Charging Mode")
        }

        Button(
            onClick = {
                appMode = UserModuleImpl()
                // appMode.displayUI()
            },
            modifier = Modifier.padding(16.dp),
        ) {
            Text("User Mode")
        }
    }
}
