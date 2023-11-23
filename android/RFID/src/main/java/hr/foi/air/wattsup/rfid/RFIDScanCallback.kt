package hr.foi.air.wattsup.rfid

interface RFIDScanCallback {
    fun onScanStarted()
    fun onScanStopped()
}
