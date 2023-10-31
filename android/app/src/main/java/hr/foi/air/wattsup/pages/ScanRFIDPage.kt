package hr.foi.air.wattsup.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.ui.component.CircleButton
import hr.foi.air.wattsup.ui.component.TopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanRFIDPage(onArrowBackClick: () -> Unit, onScanRFID: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.charger_mode)) },
                navigationIcon = {
                    IconButton(onClick = { onArrowBackClick() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
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
            var initiallyScanned by remember { mutableStateOf(false) }
            var scanning by remember { mutableStateOf(false) }
            var scanSuccess by remember { mutableStateOf(false) }
            var scanAttemptCoroutine by remember { mutableStateOf<Job?>(null) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (!scanning && !scanSuccess) {
                    CircleButton(
                        "Scan RFID card",
                        {
                            scanning = true
                            scanAttemptCoroutine = CoroutineScope(Dispatchers.Default).launch {
                                delay(5000)
                                scanning = false
                                initiallyScanned = true
                                scanSuccess = false
                            }
                            Log.i("MSG", "Coroutine " + scanAttemptCoroutine?.isActive)
                        },
                        null,
                        null,
                    )
                    Spacer(
                        modifier = Modifier.height(30.dp),
                    )
                } else {
                    // This button is only for testing purposes in place of touching the phone
                    // with a real RFID card, will be replaced with logic to detect RFID cards
                    // once we can test with them
                    Button(
                        content = { Text(text = "Click for successful scan or wait 5 seconds") },
                        onClick = {
                            scanAttemptCoroutine?.cancel()

                            scanning = false
                            initiallyScanned = true
                            scanSuccess = true

                            onScanRFID()
                        },
                        modifier = Modifier.padding(vertical = 30.dp),
                    )
                }
                Text(
                    text = if (!scanning) {
                        if (!initiallyScanned) {
                            ""
                        } else if (scanSuccess) {
                            "Scan successful"
                        } else {
                            "Unable to scan RFID card"
                        }
                    } else {
                        "Scanning for RFID card..."
                    },
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

@Preview
@Composable
fun ScanRFIDPagePreview() {
    ScanRFIDPage({}, {})
}
