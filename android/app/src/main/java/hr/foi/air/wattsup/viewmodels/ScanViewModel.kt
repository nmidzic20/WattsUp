import android.app.Application
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hr.foi.air.wattsup.ble.BLEManager
import hr.foi.air.wattsup.ble.BLEScanCallback
import hr.foi.air.wattsup.ble.PermissionCallback
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScanViewModel(application: Application) : AndroidViewModel(application) {
    private val targetDeviceAddress = "AC:23:3F:AB:9B:9F"
    private var scanAttemptCoroutine: Job? = null
    private var scanTimeoutJob: Job? = null

    private val context = application.applicationContext

    private val _scanning = MutableLiveData(false)
    val scanning: LiveData<Boolean> get() = _scanning

    private val _scanSuccess = MutableLiveData(false)
    val scanSuccess: LiveData<Boolean> get() = _scanSuccess

    private val _userMessage = MutableLiveData("")
    val userMessage: LiveData<String> get() = _userMessage

    private val _includeTestButton = MutableLiveData(true)
    val includeTestButton: LiveData<Boolean> get() = _includeTestButton

    val bleManager = BLEManager(
        context,
        object : PermissionCallback {
            override fun onPermissionGranted(permission: String) {
                // Handle permission granted
            }

            override fun onPermissionDenied(permission: String) {
                // Handle permission denied
            }
        },
    )

    fun getBluetoothStatusMessage(): String =
        if (!bleManager.isBluetoothSupported()) {
            "Bluetooth is not supported on this device"
        } else if (!bleManager.isBluetoothEnabled()) {
            "Bluetooth is not enabled on this device"
        } else {
            "Bluetooth is supported and enabled on this device"
        }

    fun startBLEScanning(onScan: () -> Unit) {
        _includeTestButton.value = false
        _scanning.value = true

        bleManager.startScanning(
            object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    handleBLEScanResult(result, onScan)
                }

                override fun onBatchScanResults(results: List<ScanResult?>?) {
                    results?.forEach { result ->
                        handleBLEScanResult(result, onScan)
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    _scanning.value = false
                    _scanSuccess.value = false
                    _userMessage.value = "Unable to scan card"
                    Log.i("BLUETOOTH", "Scanning failed, error code: $errorCode")
                }
            },
            object : BLEScanCallback {
                override fun onScanStarted() {
                    scanTimeoutJob = viewModelScope.launch {
                        // Stop scanning after 5 seconds if no device is found
                        delay(5000)
                        if (_scanning.value == true && !_scanSuccess.value!!) {
                            bleManager.stopScanning()
                            _scanning.value = false
                            _scanSuccess.value = false
                            _userMessage.value = if (!bleManager.isBluetoothSupported()) {
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

    fun startRFIDScanning() {
        scanTimeoutJob?.cancel()

        _includeTestButton.value = true
        _scanning.value = true
        scanAttemptCoroutine = viewModelScope.launch {
            delay(5000)
            _scanning.value = false
            _scanSuccess.value = false
            _userMessage.value = "Unable to scan card"
        }
    }

    fun cancelScanAttempt(onScan: () -> Unit) {
        scanAttemptCoroutine?.cancel()
        _scanning.value = false
        _scanSuccess.value = true
        _userMessage.value = "Scan successful"

        onScan()
        _scanSuccess.value =
            false // After navigating away, reset so buttons are visible for next scanning
    }

    private fun handleBLEScanResult(result: ScanResult?, onScan: () -> Unit) {
        if (result != null) {
            val device = result.device
            if (device.address == targetDeviceAddress) {
                // The target BLE device is detected
                _scanSuccess.value = true
                bleManager.stopScanning()
                _scanning.value = false
                _userMessage.value = "Scan successful"
                onScan()
                _scanSuccess.value =
                    false // After navigating away, reset so buttons are visible for next scanning
            }
        }
    }
}
