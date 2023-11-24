package hr.foi.air.wattsup.rfid

import android.app.Activity
import android.app.PendingIntent
import android.bluetooth.le.ScanCallback
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.provider.Settings
import android.util.Log

class RFIDManager(
    private val context: Context,
    private val permissionCallback: PermissionCallbackRFID? = null,
) {
    private val nfcManager = context.getSystemService(Context.NFC_SERVICE) as NfcManager?
    private val nfcAdapter: NfcAdapter? = nfcManager?.defaultAdapter
    private var pendingIntent: PendingIntent? = null
    private var filters: Array<IntentFilter>? = null
    private var techLists: Array<Array<String>>? = null

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

        val intent = Intent(context, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED))
        techLists = arrayOf(arrayOf(NfcA::class.java.name))

        disableNFC()
    }

    private fun enableNFC() {
        nfcAdapter?.enableForegroundDispatch(
            context as Activity,
            pendingIntent,
            filters,
            techLists
        )
    }

    private fun disableNFC() {
        nfcAdapter?.disableForegroundDispatch(context as Activity)
    }

    fun showEnableRFIDOption(context: Context) {
        val enableNfcIntent = Intent(Settings.ACTION_NFC_SETTINGS)
        (context as? Activity)?.startActivityForResult(
            enableNfcIntent,
            REQUEST_ENABLE_RFID,
        )
    }

    fun startScanning(scanCallback: ScanCallback, rfidScanCallback: RFIDScanCallback?) {
        enableNFC()

    }

    fun stopScanning() {
        disableNFC()
    }
}
