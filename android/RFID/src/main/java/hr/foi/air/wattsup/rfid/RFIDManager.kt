package hr.foi.air.wattsup.rfid

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.provider.Settings

class RFIDManager(private val context: Context) {

    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)

    fun scanRFID(onResult: (RFIDScanResult) -> Unit) {
        if (nfcAdapter == null) {
            onResult(RFIDScanResult.NotSupported)
            return
        }

        if (!nfcAdapter.isEnabled) {
            onResult(RFIDScanResult.NFCTurnedOff)
            openNFCSettings()
            return
        }
    }

    private fun openNFCSettings() {
        val intent = Intent(Settings.ACTION_NFC_SETTINGS)
        context.startActivity(intent)
    }
}

sealed class RFIDScanResult {
    object ValidCard : RFIDScanResult()
    object InvalidCard : RFIDScanResult()
    object NothingScanned : RFIDScanResult()
    object NotSupported : RFIDScanResult()
    object NFCTurnedOff : RFIDScanResult()
}
