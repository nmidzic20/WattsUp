package hr.foi.air.wattsup.rfid

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
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
    private var nfcManager: NfcManager? =
        context.getSystemService(Context.NFC_SERVICE) as NfcManager?
    private var nfcAdapter: NfcAdapter? = nfcManager?.defaultAdapter

    init {
        initialize()
    }

    companion object {
        private const val REQUEST_ENABLE_RFID = 1
    }

    override fun getRequiredPermissions(): List<String> = listOf(
        Manifest.permission.NFC,
    )

    override fun getStateReceiver(): BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == NfcAdapter.ACTION_ADAPTER_STATE_CHANGED) {
                val state = intent.getIntExtra(
                    NfcAdapter.EXTRA_ADAPTER_STATE,
                    NfcAdapter.STATE_OFF,
                )
                Log.i("SCAN_CARD STATE CHANGE NFC?", state.toString())
                // Whenever NFC is turned off or on, update NFC adapter and scanner so that scanning works properly
                initialize()
            }
        }
    }

    override fun getAction(): String = NfcAdapter.ACTION_ADAPTER_STATE_CHANGED

    override fun getName(): String = "RFID"

    override fun isCardSupportAvailableOnDevice(): Boolean {
        return nfcAdapter != null
    }

    override fun isCardSupportEnabledOnDevice(): Boolean {
        return nfcAdapter?.isEnabled == true
    }

    override fun initialize() {
        initializeRFIDComponents()

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

    private fun initializeRFIDComponents() {
        if (nfcManager == null) {
            nfcManager = context.getSystemService(Context.NFC_SERVICE) as NfcManager?
        }

        if (nfcAdapter == null) {
            nfcAdapter = nfcManager?.defaultAdapter
        }
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
