package hr.foi.air.wattsup.core

import android.content.BroadcastReceiver
import android.content.Context

interface CardManager {
    fun initialize()
    fun getRequiredPermissions(): List<String>
    fun getStateReceiver(): BroadcastReceiver
    fun getAction(): String
    fun getName(): String
    fun isCardSupportAvailableOnDevice(): Boolean
    fun isCardSupportEnabledOnDevice(): Boolean
    fun showEnableCardSupportOption(context: Context)
    fun startScanningForCard(cardScanCallback: CardScanCallback?)
    fun stopScanningForCard()
}
