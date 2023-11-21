package hr.foi.air.wattsup.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.rfid.RFIDManager
import hr.foi.air.wattsup.rfid.RFIDScanResult
import hr.foi.air.wattsup.ui.component.CircleButton
import hr.foi.air.wattsup.ui.component.TopAppBar
import kotlinx.coroutines.Job


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanRFIDPage(onArrowBackClick: () -> Unit, onScanRFID: () -> Unit) {
    val rfidManager = RFIDManager(
        LocalContext.current
    )

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
            var includeTestButton by remember { mutableStateOf(true) }
            var scanResultText by remember { mutableStateOf("") }

            val onRFIDClick = {
                rfidManager.scanRFID { result ->
                    when (result) {
                        is RFIDScanResult.ValidCard -> {
                            scanning = false
                            initiallyScanned = true
                            scanSuccess = true
                            onScanRFID()
                        }
                        is RFIDScanResult.InvalidCard -> {
                            scanResultText = "Register RFID card in User mode"
                            scanning = false
                            initiallyScanned = true
                            scanSuccess = false
                        }
                        is RFIDScanResult.NothingScanned -> {
                            scanResultText = "RFID card not detected"
                            scanning = false
                            initiallyScanned = true
                            scanSuccess = false
                        }
                        is RFIDScanResult.NotSupported -> {
                            scanResultText = "Scanning RFID is not supported on this phone"
                            scanning = false
                            initiallyScanned = true
                            scanSuccess = false
                        }
                        is RFIDScanResult.NFCTurnedOff -> {
                            scanResultText = "Please turn on NFC"
                            scanning = false
                            initiallyScanned = true
                            scanSuccess = false
                        }
                    }
                }
            }


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
                            includeTestButton = false
                            scanning = true
                            onRFIDClick()
                        },
                        null,
                        null,
                        Modifier
                            .size(220.dp)
                            .padding(16.dp),
                    )
                    Spacer(
                        modifier = Modifier.height(30.dp),
                    )
                } else {
                    // This button is only for testing purposes in place of touching the phone
                    // with a real RFID card, will be replaced with logic to detect RFID cards
                    // once we can test with them
                    if (includeTestButton) {
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
                }
                Text(
                    text = if (!scanning) {
                        if (!initiallyScanned) {
                            ""
                        } else if (scanSuccess) {
                            "Scan successful"
                        } else {
                            scanResultText
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
