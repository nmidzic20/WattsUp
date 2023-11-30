package hr.foi.air.wattsup.rfid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.provider.Settings
import android.util.Log
import hr.foi.air.wattsup.core.CardManager
import hr.foi.air.wattsup.core.CardScanCallback

class RFIDManager(
    private val context: Context,
) : CardManager {
    private val nfcManager = context.getSystemService(Context.NFC_SERVICE) as NfcManager?
    private val nfcAdapter: NfcAdapter? = nfcManager?.defaultAdapter

    init {
        initialize()
    }

    companion object {
        private const val REQUEST_ENABLE_RFID = 1
    }

    override fun getName(): String = "RFID"

    override fun isCardSupportAvailableOnDevice(): Boolean {
        return nfcAdapter != null
    }

    override fun isCardSupportEnabledOnDevice(): Boolean {
        return nfcAdapter?.isEnabled == true
    }

    override fun initialize() {
        if (!isCardSupportAvailableOnDevice()) {
            val message = "RFID not supported on this device"
            Log.i("RFID", message)
            return
        }

        if (!isCardSupportEnabledOnDevice()) {
            val message = "Not enabled - please enable RFID/NFC in Settings on your device"
            Log.i("RFID", message)
            return
        }
        Log.i("RFID", "RFID supported and enabled")
    }

    override fun showEnableCardSupportOption(context: Context) {
        val enableNfcIntent = Intent(Settings.ACTION_NFC_SETTINGS)
        (context as? Activity)?.startActivityForResult(
            enableNfcIntent,
            REQUEST_ENABLE_RFID,
        )
    }

    override fun startScanningForCard(rfidScanCallback: CardScanCallback?) {
        try {
            CallbackHolder.rfidScanCallback = rfidScanCallback

            val intent = Intent(context, NfcReaderActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("RFID", "Error: " + e.message)
            rfidScanCallback?.onScanFailed("Error starting RFID scan: ${e.message}")
        }
    }

    override fun stopScanningForCard() {
        Log.i("RFID", "Stop scan")
    }
}
