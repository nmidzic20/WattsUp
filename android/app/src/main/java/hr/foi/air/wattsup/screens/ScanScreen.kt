package hr.foi.air.wattsup.screens

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.ble.BLEManager
import hr.foi.air.wattsup.ble.BLEScanCallback
import hr.foi.air.wattsup.ble.PermissionCallback
import hr.foi.air.wattsup.ui.component.CircleButton
import hr.foi.air.wattsup.ui.component.TopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(onArrowBackClick: () -> Unit, onScan: () -> Unit) {
    val bleManager = BLEManager(
        LocalContext.current,
        object : PermissionCallback {

            override fun onPermissionGranted(permission: String) {
                // Handle permission granted
            }

            override fun onPermissionDenied(permission: String) {
                // Handle permission denied
            }
        },
    )
    val targetDeviceAddress = "AC:23:3F:AB:9B:9F"
    var bluetoothStatusMessage = if (!bleManager.isBluetoothSupported()) {
        "Bluetooth is not supported on this device"
    } else if (!bleManager.isBluetoothEnabled()) {
        "Bluetooth is not enabled on this device"
    } else {
        "Bluetooth is supported and enabled on this device"
    }

    var userMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(snackbarHostState) {
        scope.launch {
            val result = snackbarHostState
                .showSnackbar(
                    message = bluetoothStatusMessage,
                    actionLabel = if (bleManager.isBluetoothSupported() && !bleManager.isBluetoothEnabled()) "Turn on Bluetooth" else "OK",
                    duration = SnackbarDuration.Indefinite,
                )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    if (bleManager.isBluetoothSupported() && !bleManager.isBluetoothEnabled()) {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.BLUETOOTH_CONNECT,
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                        }
                        (context as? Activity)?.startActivityForResult(
                            enableBtIntent,
                            4,
                        )
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
            var scanning by remember { mutableStateOf(false) }
            var scanSuccess by remember { mutableStateOf(false) }
            var scanAttemptCoroutine by remember { mutableStateOf<Job?>(null) }
            var includeTestButton by remember { mutableStateOf(true) }

            val onBLEClick = {
                bleManager.startScanning(
                    object : ScanCallback() {
                        override fun onScanResult(callbackType: Int, result: ScanResult?) {
                            scanSuccess = if (result != null) {
                                val device = result.device
                                device.address == targetDeviceAddress
                            } else {
                                false
                            }
                            if (scanSuccess) {
                                bleManager.stopScanning()
                                scanning = false
                                userMessage = "Scan successful"
                                onScan()
                            } else {
                                // Scanning continues until right device found or until time allotted for scanning is up
                            }
                            Log.i("BLUETOOTH", "Scanning result: ${result?.device}")
                        }

                        override fun onBatchScanResults(results: List<ScanResult?>?) {
                            results?.forEach { result ->
                                scanSuccess = if (result != null) {
                                    val device = result.device
                                    device.address == targetDeviceAddress
                                } else {
                                    false
                                }
                                Log.i("BLUETOOTH", "Scanning result from batch: ${result?.device}")
                            }
                            if (scanSuccess) {
                                bleManager.stopScanning()
                                scanning = false
                                userMessage = "Scan successful"
                                onScan()
                            } else {
                                // Scanning continues until right device found or until time allotted for scanning is up
                            }
                        }

                        override fun onScanFailed(errorCode: Int) {
                            scanning = false
                            scanSuccess = false
                            userMessage = "Unable to scan card"
                            Log.i("BLUETOOTH", "Scanning failed, error code: $errorCode")
                        }
                    },
                    object : BLEScanCallback {
                        private var scanTimeoutJob: Job? = null

                        override fun onScanStarted() {
                            scanTimeoutJob = scope.launch {
                                // Stop scanning after 5 seconds if no device is found
                                delay(5000)
                                if (scanning && !scanSuccess) {
                                    bleManager.stopScanning()
                                    scanning = false
                                    scanSuccess = false
                                    userMessage = if (!bleManager.isBluetoothSupported()) {
                                        "Bluetooth is not supported on this device"
                                    } else if (!bleManager.isBluetoothEnabled()) {
                                        "Bluetooth is not enabled on this device"
                                    } else {
                                        "No BLE card found"
                                    }
                                }
                            }
                        }

                        override fun onScanStopped() {
                            scanTimeoutJob?.cancel()
                        }
                    },
                )
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
                            includeTestButton = true
                            scanning = true
                            scanAttemptCoroutine = CoroutineScope(Dispatchers.Default).launch {
                                delay(5000)
                                scanning = false
                                scanSuccess = false
                                userMessage = "Unable to scan card"
                            }
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
                            includeTestButton = false
                            scanning = true
                            onBLEClick()
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
                    if (includeTestButton) {
                        Button(
                            content = { Text(text = "Click for successful scan or wait 5 seconds") },
                            onClick = {
                                scanAttemptCoroutine?.cancel()

                                scanning = false
                                scanSuccess = true

                                onScan()
                            },
                            modifier = Modifier.padding(vertical = 30.dp),
                        )
                    }
                }
                Text(
                    text = if (!scanning) {
                        userMessage
                    } else {
                        "Scanning for card..."
                    },
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

@Preview
@Composable
fun ScanScreenPreview() {
    ScanScreen({}, {})
}
