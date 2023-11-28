package hr.foi.air.wattsup.rfid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.provider.Settings
import android.util.Log

class RFIDManager(
    private val context: Context,
) {
    private val nfcManager = context.getSystemService(Context.NFC_SERVICE) as NfcManager?
    private val nfcAdapter: NfcAdapter? = nfcManager?.defaultAdapter

    init {
        initializeRFID()
    }

    companion object {
        private const val REQUEST_ENABLE_RFID = 1
    }

    fun isRFIDSupported(): Boolean {
        return nfcAdapter != null
    }

    fun isRFIDEnabled(): Boolean {
        return nfcAdapter?.isEnabled == true
    }

    private fun initializeRFID() {
        if (!isRFIDSupported()) {
            val message = "RFID not supported on this device"
            Log.i("RFID", message)
            return
        }

        if (!isRFIDEnabled()) {
            val message = "Not enabled - please enable RFID/NFC in Settings on your device"
            Log.i("RFID", message)
            return
        }
        Log.i("RFID", "RFID supported and enabled")
    }

    fun showEnableRFIDOption(context: Context) {
        val enableNfcIntent = Intent(Settings.ACTION_NFC_SETTINGS)
        (context as? Activity)?.startActivityForResult(
            enableNfcIntent,
            REQUEST_ENABLE_RFID,
        )
    }

    fun startScanning(rfidScanCallback: RFIDScanCallback?) {
        try {
            CallbackHolder.rfidScanCallback = rfidScanCallback

            val intent = Intent(context, NfcReaderActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("RFID", "Error: " + e.message)
            rfidScanCallback?.onRFIDScanError("Error starting RFID scan: ${e.message}")
        }
    }
}
