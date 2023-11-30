import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hr.foi.air.wattsup.ble.BLEManager
import hr.foi.air.wattsup.core.CardScanCallback
import hr.foi.air.wattsup.network.CardService
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.rfid.RFIDManager
import hr.foi.air.wattsup.utils.HexUtils
import hr.foi.air.wattsup.utils.UserCard
import kotlinx.coroutines.Dispatchers
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

    private val _cardList = MutableLiveData<List<Card>>(emptyList())

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
    )

    val rfidManager = RFIDManager(context)

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
                            _cardList.value =
                                cardList.map { card -> card!! }
                            Log.i("CARD", "Received cards, cards addresses: ")
                            _cardList.value!!.forEach { Log.i("CARD ADDRESS", "${it.value}") }
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
        if (!bleManager.isCardSupportAvailableOnDevice()) {
            "Bluetooth is not supported on this device"
        } else if (!bleManager.isCardSupportEnabledOnDevice()) {
            "Bluetooth is not enabled on this device"
        } else {
            if (scanning) "No registered BLE card found" else "Bluetooth is supported and enabled on this device"
        }

    fun getRFIDStatusMessage(scanning: Boolean): String =
        if (!rfidManager.isCardSupportAvailableOnDevice()) {
            "RFID is not supported on this device"
        } else if (!rfidManager.isCardSupportEnabledOnDevice()) {
            "RFID is not enabled on this device"
        } else {
            if (scanning) "No RFID card found" else "RFID is supported and enabled on this device"
        }

    fun startBLEScanning(onScan: () -> Unit) {
        if (!bleManager.isCardSupportEnabledOnDevice()) {
            bleManager.stopScanningForCard()
            _scanning.value = false
            _scanSuccess.value = false
            _userMessage.value = "Bluetooth is not enabled on this device"
            return
        }

        _includeTestButton.value = false
        _scanning.value = true

        bleManager.startScanningForCard(
            object : CardScanCallback {
                override fun onScanResult(cardAddress: Any) {
                    handleBLEScanResult(cardAddress, onScan)
                }

                override fun onBatchScanResults(results: List<Any>?) {
                    results?.forEach { result ->
                        handleBLEScanResult(result, onScan)
                    }
                }

                override fun onScanFailed(errorCode: String) {
                    _scanning.value = false
                    _scanSuccess.value = false
                    _userMessage.value = "Unable to scan card"
                    Log.i("BLUETOOTH", "Scanning failed, error code: $errorCode")
                }

                override fun onScanStarted() {
                    BLEscanTimeoutJob = viewModelScope.launch {
                        // Stop scanning after 5 seconds if no device is found
                        delay(5000)
                        if (_scanning.value == true && !_scanSuccess.value!!) {
                            bleManager.stopScanningForCard()
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
        if (!rfidManager.isCardSupportEnabledOnDevice()) {
            _scanning.value = false
            _scanSuccess.value = false
            _userMessage.value = "RFID is not enabled on this device"
            return
        }
        _includeTestButton.value = false
        _scanning.value = true

        rfidManager.startScanningForCard(object : CardScanCallback {
            override fun onScanStarted() {
                TODO("Not yet implemented")
            }

            override fun onScanStopped() {
                TODO("Not yet implemented")
            }

            override fun onScanResult(cardAddress: Any) {
                handleRFIDScanResult(cardAddress, onScan)
            }

            override fun onBatchScanResults(results: List<Any>?) {
                TODO("Not yet implemented")
            }

            override fun onScanFailed(errorMessage: String) {
                if (errorMessage == "RFID scan timed out") {
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

    private fun handleBLEScanResult(result: Any, onScan: () -> Unit) {
        var deviceAddress: String = ""
        if (result is ByteArray) {
            deviceAddress = HexUtils.formatHexToPrefix(HexUtils.bytesToHexString(result))
        } else if (result is String) {
            deviceAddress = HexUtils.formatHexToPrefix(result)
        }

        Log.i("BLUETOOTH", "Scanned: $deviceAddress")

        if (deviceAddressMatchesDatabaseCardValue(deviceAddress)) {
            // The target BLE device is detected
            _scanSuccess.value = true
            bleManager.stopScanningForCard()
            _scanning.value = false
            _userMessage.value = "Scan successful"
            onScan()
            _scanSuccess.value =
                false // After navigating away, reset so buttons are visible for next scanning
        }
    }

    private fun handleRFIDScanResult(result: Any, onScan: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            Log.i("RFID", "UID 1: $result")

            var cardAddress: String = ""
            if (result is ByteArray) {
                cardAddress = HexUtils.formatHexToPrefix(HexUtils.bytesToHexString(result))
            } else if (result is String) {
                cardAddress = result
            }

            Log.i("RFID", "UID 2: $cardAddress")
            if (deviceAddressMatchesDatabaseCardValue(cardAddress)) {
                _scanSuccess.value = true
                _scanning.value = false
                _userMessage.value = "Scan successful"
                onScan()
                _scanSuccess.value =
                    false // After navigating away, reset so buttons are visible for the next scanning
            } else {
                _scanning.value = false
                _scanSuccess.value = false
                _userMessage.value = "Register your card before using it"
            }
        }
    }

    private fun deviceAddressMatchesDatabaseCardValue(deviceAddress: String): Boolean {
        val cardList = _cardList.value ?: emptyList()
        val card = cardList.find { card -> HexUtils.compareHexStrings(card.value, deviceAddress) }
        return if (card != null) {
            Log.i("CARD_MATCH", "ID: ${card.id} | VALUE: ${card.value}")
            UserCard.userCard.value = card
            true
        } else {
            false
        }
    }
}
