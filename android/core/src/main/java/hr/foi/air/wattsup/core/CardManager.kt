package hr.foi.air.wattsup.core

import android.content.Context

interface CardManager {
    fun initialize()
    fun isCardSupportAvailableOnDevice(): Boolean
    fun isCardSupportEnabledOnDevice(): Boolean
    fun showEnableCardSupportOption(context: Context)

    // fun checkAndRequestPermission(permission: String, requestCode: Int)
    fun startScanningForCard(cardScanCallback: CardScanCallback)
    fun stopScanningForCard()
    // fun connectToCard(/**/)
    // fun disconnectFromCard()
    // fun sendDataToCard(/)
    // fun readDataFromCard()
}
