package hr.foi.air.wattsup.rfid

interface RFIDScanCallback {
    fun onRFIDScanResult(uid: String)
    fun onRFIDScanError(errorMessage: String)
}
