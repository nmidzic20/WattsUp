package hr.foi.air.wattsup.screens

import ScanViewModel
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.ui.component.CircleButton
import hr.foi.air.wattsup.ui.component.TopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(onArrowBackClick: () -> Unit, onScan: () -> Unit, viewModel: ScanViewModel) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val userMessage by viewModel.userMessage.observeAsState()
    var bluetoothStatusMessage by remember {
        mutableStateOf(viewModel.getBluetoothStatusMessage(false))
    }
    var rfidStatusMessage by remember {
        mutableStateOf(viewModel.getRFIDStatusMessage(false))
    }

    val scanning by viewModel.scanning.observeAsState()
    val scanSuccess by viewModel.scanSuccess.observeAsState()
    val includeTestButton by viewModel.includeTestButton.observeAsState()

    LaunchedEffect(snackbarHostState) {
        scope.launch {
            val bleResult = snackbarHostState
                .showSnackbar(
                    message = bluetoothStatusMessage,
                    actionLabel = if (viewModel.bleManager.isBluetoothSupported() && !viewModel.bleManager.isBluetoothEnabled()) "Turn on Bluetooth" else "OK",
                    duration = SnackbarDuration.Indefinite,
                )
            when (bleResult) {
                SnackbarResult.ActionPerformed -> {
                    if (viewModel.bleManager.isBluetoothSupported() && !viewModel.bleManager.isBluetoothEnabled()) {
                        viewModel.bleManager.showEnableBluetoothOption(context)
                    }
                }
                SnackbarResult.Dismissed -> {
                }
            }

            val rfidResult = snackbarHostState.showSnackbar(
                message = rfidStatusMessage.toString(),
                actionLabel = if (viewModel.rfidManager.isRFIDSupported() && !viewModel.rfidManager.isRFIDEnabled()) "Enable RFID/NFC" else "OK",
                duration = SnackbarDuration.Indefinite,
            )
            when (rfidResult) {
                SnackbarResult.ActionPerformed -> {
                    if (viewModel.rfidManager.isRFIDSupported() && !viewModel.rfidManager.isRFIDEnabled()) {
                        viewModel.rfidManager.showEnableRFIDOption(context)
                    }
                }
                SnackbarResult.Dismissed -> {
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (!scanning!! && !scanSuccess!!) {
                    CircleButton(
                        "Scan RFID card",
                        {
                            viewModel.startRFIDScanning(onScan)
                        },
                        null,
                        null,
                        Modifier.size(220.dp)
                            .padding(16.dp),
                    )
                    Spacer(
                        modifier = Modifier.height(30.dp),
                    )
                    CircleButton(
                        "Scan BLE card",
                        {
                            viewModel.startBLEScanning(onScan)
                        },
                        null,
                        null,
                        Modifier.size(220.dp)
                            .padding(16.dp),
                    )
                } else {
                    // This button is only for testing purposes in place of touching the phone
                    // with a real RFID card, will be replaced with logic to detect RFID cards
                    // once we can test with them
                    if (includeTestButton == true) {
                        Button(
                            content = { Text(text = "Click for successful scan or wait 5 seconds") },
                            onClick = {
                                viewModel.cancelRFIDScanAttempt(onScan)
                            },
                            modifier = Modifier.padding(vertical = 30.dp),
                        )
                    }
                }
                Text(
                    text = if (!scanning!!) {
                        userMessage!!
                    } else {
                        "Scanning for card..."
                    },
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}
