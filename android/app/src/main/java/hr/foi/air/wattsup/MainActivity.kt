package hr.foi.air.wattsup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.charging.ChargingModuleImpl
import hr.foi.air.core.AppMode
import hr.foi.air.user_mode.UserModuleImpl
import hr.foi.air.wattsup.ui.theme.WattsUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WattsUpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    UserModeSelectionScreen()
                }
            }
        }
    }
}

@Composable
fun UserModeSelectionScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
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

@Preview(showBackground = true)
@Composable
fun UserModeSelectionScreenPreview() {
    WattsUpTheme {
        UserModeSelectionScreen()
    }
}
