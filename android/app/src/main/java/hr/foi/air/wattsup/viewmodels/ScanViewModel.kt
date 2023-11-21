package hr.foi.air.wattsup.viewmodels

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.wattsup.ble.BLEManager
import hr.foi.air.wattsup.ble.BLEScanCallback
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScanViewModel : ViewModel() {
    var scanning by mutableStateOf(false)
    var scanSuccess by mutableStateOf(false)
    var userMessage by mutableStateOf("")
    private var scanAttemptCoroutine: Job? = null

    private val targetDeviceAddress = "AC:23:3F:AB:9B:9F"

    fun startScanning(bleManager: BLEManager, onScan: () -> Unit) {
        scanning = true
        scanAttemptCoroutine = viewModelScope.launch {
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

        bleManager.startScanning(
            object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    handleScanResult(result, bleManager, onScan)
                }

                override fun onBatchScanResults(results: List<ScanResult?>?) {
                    results?.forEach { result ->
                        handleScanResult(result, bleManager, onScan)
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
                    scanTimeoutJob = viewModelScope.launch {
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

    private fun handleScanResult(result: ScanResult?, bleManager: BLEManager, onScan: () -> Unit) {
        if (result != null) {
            val device = result.device
            if (device.address == targetDeviceAddress) {
                // The target BLE device is detected
                scanSuccess = true
                bleManager.stopScanning()
                scanning = false
                userMessage = "Scan successful"
                onScan()
            }
        }
    }

    fun cancelScanAttempt() {
        scanAttemptCoroutine?.cancel()
    }
}
