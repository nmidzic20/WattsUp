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
import hr.foi.air.wattsup.rfid.RFIDManager
import hr.foi.air.wattsup.rfid.RFIDScanCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScanViewModel(application: Application) : AndroidViewModel(application) {
    private val BLEtargetDeviceAddress = "AC:23:3F:AB:9B:9F"
    private var RFIDscanTimeoutJob: Job? = null
    private var BLEscanTimeoutJob: Job? = null

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

    val rfidManager = RFIDManager(context)

    fun getBluetoothStatusMessage(scanning: Boolean): String =
        if (!bleManager.isBluetoothSupported()) {
            "Bluetooth is not supported on this device"
        } else if (!bleManager.isBluetoothEnabled()) {
            "Bluetooth is not enabled on this device"
        } else {
            if (scanning) "No BLE card found" else "Bluetooth is supported and enabled on this device"
        }

    fun getRFIDStatusMessage(scanning: Boolean): String =
        if (!rfidManager.isRFIDSupported()) {
            "RFID is not supported on this device"
        } else if (!rfidManager.isRFIDEnabled()) {
            "RFID is not enabled on this device"
        } else {
            if (scanning) "No RFID card found" else "RFID is supported and enabled on this device"
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
                    BLEscanTimeoutJob = viewModelScope.launch {
                        // Stop scanning after 5 seconds if no device is found
                        delay(5000)
                        if (_scanning.value == true && !_scanSuccess.value!!) {
                            bleManager.stopScanning()
                            _scanning.value = false
                            _scanSuccess.value = false
                            _userMessage.value = getBluetoothStatusMessage(true)
                        }
                    }
                }

                override fun onScanStopped() {
                    BLEscanTimeoutJob?.cancel()
                }
            },
        )
    }

    fun startRFIDScanning(onScan: () -> Unit) {
        if (!rfidManager.isRFIDEnabled()) {
            _scanning.value = false
            _scanSuccess.value = false
            _userMessage.value = "RFID is not enabled on this device"
            return
        }
        _includeTestButton.value = false
        _scanning.value = true

        rfidManager.startScanning(object : RFIDScanCallback {
            override fun onRFIDScanResult(uid: ByteArray) {
                handleRFIDScanResult(onScan, uid)
            }

            override fun onRFIDScanError(errorMessage: String) {
                if(errorMessage == "RFID scan timed out"){
                    _scanning.value = false
                    _scanSuccess.value = false
                    _userMessage.value = getRFIDStatusMessage(true)
                } else {
                    _scanning.value = false
                    _scanSuccess.value = false
                    _userMessage.value = "Unable to scan card"
                    Log.i("RFID", "Scanning failed, error message: $errorMessage")
                }
            }
        })
    }

    fun cancelRFIDScanAttempt(onScan: () -> Unit) {
        RFIDscanTimeoutJob?.cancel()
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
            if (device.address == BLEtargetDeviceAddress) {
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

    private fun handleRFIDScanResult(onScan: () -> Unit, uid: ByteArray) {
        viewModelScope.launch(Dispatchers.Main) {
            Log.i("RFID", "UID: $uid")
            _scanSuccess.value = true
            _scanning.value = false
            _userMessage.value = "Scan successful"
            onScan()
            _scanSuccess.value =
                false // After navigating away, reset so buttons are visible for the next scanning
        }
    }
}
