package hr.foi.air.wattsup.rfid

interface RFIDScanCallback {
    fun onRFIDScanResult(uid: ByteArray)
    fun onRFIDScanError(errorMessage: String)
}
