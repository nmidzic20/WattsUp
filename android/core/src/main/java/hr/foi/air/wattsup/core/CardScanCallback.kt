package hr.foi.air.wattsup.core

interface CardScanCallback {
    fun onScanStarted()
    fun onScanStopped()
    fun onScanResult(cardAddress: Any)
    fun onScanFailed(error: String)
}
