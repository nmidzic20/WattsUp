package hr.foi.air.wattsup.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.wattsup.core.CardManager
import hr.foi.air.wattsup.core.CardScanCallback
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.repository.WattsUpRepository
import hr.foi.air.wattsup.utils.HexUtils
import hr.foi.air.wattsup.utils.LastNewCard
import hr.foi.air.wattsup.utils.UserCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScanViewModel(private val repository: WattsUpRepository) : ViewModel() {

    private var scanTimeoutJob: Job? = null
    private val _scanning = MutableLiveData(false)
    val scanning: LiveData<Boolean> get() = _scanning
    private val _scanSuccess = MutableLiveData(false)
    val scanSuccess: LiveData<Boolean> get() = _scanSuccess
    private val _userMessage = MutableLiveData("")
    val userMessage: LiveData<String> get() = _userMessage

    fun getStatusMessage(
        scanning: Boolean,
        lastNewCard: LastNewCard?,
        cardManager: CardManager,
    ): String =
        if (!cardManager.isCardSupportAvailableOnDevice()) {
            "${cardManager.getName()} is not supported on this device"
        } else if (!cardManager.isCardSupportEnabledOnDevice()) {
            "${cardManager.getName()} is not enabled on this device"
        } else {
            if (scanning) "No ${if (lastNewCard == null) "registered " else ""}${cardManager.getName()} card found" else "${cardManager.getName()} is supported and enabled on this device"
        }

    fun startScanning(cardManager: CardManager, onScan: () -> Unit, lastNewCard: LastNewCard?) {
        if (!cardManager.isCardSupportEnabledOnDevice()) {
            cardManager.stopScanningForCard()
            _scanning.value = false
            _scanSuccess.value = false
            _userMessage.value = "${cardManager.getName()} is not enabled on this device"
            viewModelScope.launch {
                // Clear the user message after 5 seconds, so that it does not stay if the user in meantime enables that card support
                delay(5000)
                _userMessage.value = ""
            }
            return
        }

        _scanning.value = true

        cardManager.startScanningForCard(
            object : CardScanCallback {
                override fun onScanResult(cardAddress: Any) {
                    handleScanResult(cardAddress, onScan, cardManager, lastNewCard)
                }

                override fun onScanFailed(error: String) {
                    _scanning.value = false
                    _scanSuccess.value = false
                    _userMessage.value = getStatusMessage(true, lastNewCard, cardManager)
                    Log.i("SCAN", "Scanning failed, error message: $error")
                }

                override fun onScanStarted() {
                    scanTimeoutJob = viewModelScope.launch {
                        // Stop scanning after 5 seconds if no device is found
                        delay(5000)
                        if (_scanning.value == true && !_scanSuccess.value!!) {
                            cardManager.stopScanningForCard()
                            _scanning.value = false
                            _scanSuccess.value = false
                            _userMessage.value = getStatusMessage(true, lastNewCard, cardManager)
                        }
                    }
                }

                override fun onScanStopped() {
                    scanTimeoutJob?.cancel()
                }
            },
        )
    }

    private fun handleScanResult(
        result: Any,
        onScan: () -> Unit,
        cardManager: CardManager,
        lastNewCard: LastNewCard?,
    ) {
        if (cardManager.scanResultRequiresAsyncHandling()) {
            viewModelScope.launch(Dispatchers.Main) {
                if (lastNewCard != null) {
                    saveCardToLastAddedCard(
                        lastNewCard,
                        result,
                        onScan,
                    ) { cardManager.stopScanningForCard() }
                } else {
                    checkedCardAddresses.clear()
                    handleScan(result, onScan, cardManager) { cardManager.stopScanningForCard() }
                }
            }
        } else {
            if (lastNewCard != null) {
                saveCardToLastAddedCard(
                    lastNewCard,
                    result,
                    onScan,
                ) { cardManager.stopScanningForCard() }
            } else {
                handleScan(result, onScan, cardManager) { cardManager.stopScanningForCard() }
            }
        }
    }

    private fun formatDeviceAddress(result: Any): String = when (result) {
        is ByteArray -> HexUtils.formatHexToPrefix(HexUtils.bytesToHexString(result))
        is String -> HexUtils.formatHexToPrefix(result)
        else -> throw IllegalArgumentException("Unsupported result type")
    }

    private fun saveCardToLastAddedCard(
        lastNewCard: LastNewCard,
        result: Any,
        onScan: () -> Unit,
        onCardFound: () -> Unit,
    ) {
        var deviceAddress: String = formatDeviceAddress(result)

        lastNewCard.setLastNewCard(Card(id = 0, value = deviceAddress, active = true))

        onCardFound()
        scanTimeoutJob?.cancel()

        _scanSuccess.value = true
        _scanning.value = false
        _userMessage.value = "Scan successful"

        onScan()

        _scanSuccess.value = false
        _userMessage.value = ""
    }

    private val checkedCardAddresses = mutableSetOf<String>()

    private fun handleScan(
        result: Any,
        onScan: () -> Unit,
        cardManager: CardManager,
        onCardFound: () -> Unit,
    ) {
        var deviceAddress: String = formatDeviceAddress(result)

        Log.i("SCAN", "Scanned: $deviceAddress")

        val onCardAuthenticated: (card: Card) -> Unit = { card ->
            onCardFound()
            scanTimeoutJob?.cancel()
            UserCard.userCard.value = card

            _scanSuccess.value = true
            _scanning.value = false
            _userMessage.value = "Scan successful"

            onScan()

            // After navigating away, reset so buttons are visible for next scanning,
            // remove past user messages and clear checkedCardAddresses set
            _scanSuccess.value = false
            _userMessage.value = ""
            checkedCardAddresses.clear()
        }

        val onCardInvalid: () -> Unit = {
            if (cardManager.scanResultRequiresAsyncHandling()) {
                onCardFound()

                _scanSuccess.value = false
                _scanning.value = false
                _userMessage.value = "Card is not registered"

                Log.i("CARD_STOP", " RFID stopped scanning")
            } else {
                Log.i("CARD_CONTINUE", "Continue scanning until BLE scantimeoutjob")
            }
        }

        val cardAddress = HexUtils.formatToDatabase(deviceAddress)

        Log.i(
            "CARD_BEFORE_RES",
            cardAddress,
        )

        deviceAddressMatchesDatabaseCardValue(cardAddress, onCardAuthenticated, onCardInvalid)
    }

    private fun deviceAddressMatchesDatabaseCardValue(
        deviceAddress: String,
        onCardAuthenticated: (card: Card) -> Unit,
        onCardInvalid: () -> Unit,
    ) {
        // prevent double checking of already scanned cards
        if (deviceAddress in checkedCardAddresses) {
            Log.i("CARD_", "$deviceAddress is in $checkedCardAddresses")
            return
        } else {
            Log.i(
                "CARD_",
                "$deviceAddress $checkedCardAddresses",
            )
        }

        val onResponse: () -> Unit = { checkedCardAddresses.add(deviceAddress) }

        repository.authenticateCard(deviceAddress, onResponse, onCardAuthenticated, onCardInvalid)
    }
}
