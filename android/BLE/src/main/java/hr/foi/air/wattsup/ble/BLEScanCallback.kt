package hr.foi.air.wattsup.ble

interface BLEScanCallback {
    fun onScanStarted()
    fun onScanStopped()
}
