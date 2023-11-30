package hr.foi.air.wattsup.core

interface CardScanCallback {
    fun onScanStarted()
    fun onScanStopped()
    fun onScanResult(cardAddress: ByteArray?)
    fun onBatchScanResults(results: List<ByteArray?>?)
    fun onScanFailed(errorCode: Int)
}
