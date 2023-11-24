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
import hr.foi.air.wattsup.network.CardService
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.utils.HexUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanViewModel(
    application: Application,
) :
    AndroidViewModel(application) {

    private val cardService: CardService = NetworkService.cardService

    private val BLEtargetDeviceAddress = "AC:23:3F:AB:9B:9F"
    private val _cardAddressList = MutableLiveData<List<String>>(emptyList())

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

    init {
        refreshCardAddressList()
    }

    fun refreshCardAddressList() {
        viewModelScope.launch {
            cardService.getCards().enqueue(object : Callback<List<Card?>> {
                override fun onResponse(call: Call<List<Card?>>, response: Response<List<Card?>>) {
                    if (response.isSuccessful) {
                        val cardList: List<Card?>? = response.body()
                        if (cardList != null) {
                            _cardAddressList.value =
                                cardList.map { card -> card?.value ?: "" }
                            Log.i("CARD", "Received cards, cards addresses: ")
                            _cardAddressList.value!!.forEach { Log.i("CARD ADDRESS", "$it") }
                        } else {
                            Log.i("CARD", "Received cards as null")
                        }
                    } else {
                        val errorBody = response.errorBody()
                        Log.i("CARD", "Handle error, error: $errorBody")
                        _userMessage.value = "Error when fetching cards: $errorBody"
                    }
                }

                override fun onFailure(call: Call<List<Card?>>, t: Throwable) {
                    Log.i("CARD", "Network request failed")
                    _userMessage.value = "Network request failed (turn on backend first)"
                }
            })
        }
    }

    fun getBluetoothStatusMessage(scanning: Boolean): String =
        if (!bleManager.isBluetoothSupported()) {
            "Bluetooth is not supported on this device"
        } else if (!bleManager.isBluetoothEnabled()) {
            "Bluetooth is not enabled on this device"
        } else {
            if (scanning) "No registered BLE card found" else "Bluetooth is supported and enabled on this device"
        }

    fun startBLEScanning(onScan: () -> Unit) {
        if (!bleManager.isBluetoothEnabled()) {
            bleManager.stopScanning()
            _scanning.value = false
            _scanSuccess.value = false
            _userMessage.value = "Bluetooth is not enabled on this device"
            return
        }

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

    fun startRFIDScanning() {
        BLEscanTimeoutJob?.cancel()

        _includeTestButton.value = true
        _scanning.value = true
        RFIDscanTimeoutJob = viewModelScope.launch {
            delay(5000)
            _scanning.value = false
            _scanSuccess.value = false
            _userMessage.value = "Unable to scan card"
        }
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
            val deviceAddress = HexUtils.formatHexToPrefix(device.address)
            Log.i("BLUETOOTH", "Scanned: $device $deviceAddress")

            if (deviceAddressMatchesDatabaseCardValue(deviceAddress)) {
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

    private fun deviceAddressMatchesDatabaseCardValue(deviceAddress: String): Boolean =
        _cardAddressList.value!!.any { HexUtils.compareHexStrings(it, deviceAddress) }
}
