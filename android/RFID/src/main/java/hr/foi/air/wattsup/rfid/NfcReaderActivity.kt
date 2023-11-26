package hr.foi.air.wattsup.rfid

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class NfcReaderActivity : Activity(), NfcAdapter.ReaderCallback {
    private var mNfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var rfidScanCallback: RFIDScanCallback? = null
    private val handler = Handler(Looper.getMainLooper())
    private val timeoutMillis = 5000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        rfidScanCallback = CallbackHolder.rfidScanCallback
    }

    override fun onStart() {
        super.onStart()
        setVisible(true)
    }

    override fun onResume() {
        super.onResume()

        mNfcAdapter?.enableReaderMode(
            this,
            this,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or
                    NfcAdapter.FLAG_READER_NFC_V or
                    NfcAdapter.FLAG_READER_NFC_BARCODE or
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )

        handler.postDelayed({
            rfidScanCallback?.onRFIDScanError("RFID scan timed out")
            finish()
        }, timeoutMillis)
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter?.disableReaderMode(this)

        handler.removeCallbacksAndMessages(null)
    }

    override fun onTagDiscovered(tag: Tag?) {
        if (tag != null) {
            handleNfcTag(tag)
        } else {
            rfidScanCallback?.onRFIDScanError("No NFC tag found")
            finish()
        }
    }
    private fun handleNfcTag(tag: Parcelable) {
        if (tag is Tag) {
            val uidBytes: ByteArray = tag.id
            val uidHexString: String = bytesToHexString(uidBytes)

            Log.d("RFID", "NFC tag UID: $uidHexString")
            rfidScanCallback?.onRFIDScanResult(uidHexString)
        } else {
            Log.e("RFID", "Invalid tag type: ${tag.javaClass.simpleName}")
            rfidScanCallback?.onRFIDScanError("Invalid tag type: ${tag.javaClass.simpleName}")
        }
        CallbackHolder.rfidScanCallback = null
        finish()
    }
    private fun bytesToHexString(bytes: ByteArray): String {
        val stringBuilder = StringBuilder()
        for (byte in bytes) {
            stringBuilder.append(String.format("%02X", byte))
            stringBuilder.append(":")
        }
        return stringBuilder.toString()
    }
}