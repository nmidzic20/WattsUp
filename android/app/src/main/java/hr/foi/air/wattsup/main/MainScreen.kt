package hr.foi.air.wattsup.main

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.charging.ChargingModuleImpl
import hr.foi.air.core.AppMode
import hr.foi.air.user_mode.UserModuleImpl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "WattsUp",

                    )
                },
                navigationIcon = {
                    IconButton(onClick = { Log.i("NAV", "menu") }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description",
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Green.copy(alpha = 0.3f),
                ),
            )
        },
        content = { padding ->
            UserModeSelectionScreen(
                modifier = Modifier
                    .padding(padding),
            )
        },
    )
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

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}
