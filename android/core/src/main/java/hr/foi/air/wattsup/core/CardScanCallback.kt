package hr.foi.air.wattsup.core

interface CardScanCallback {
    fun onScanStarted()
    fun onScanStopped()
    fun onScanResult(cardAddress: Any)

    // fun onBatchScanResults(results: List<Any>?)
    fun onScanFailed(error: String)
}
