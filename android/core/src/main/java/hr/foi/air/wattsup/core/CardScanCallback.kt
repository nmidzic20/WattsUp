package hr.foi.air.wattsup.core

import android.bluetooth.le.ScanResult

interface CardScanCallback {
    fun onScanStarted()
    fun onScanStopped()
    fun onScanResult(callbackType: Int, result: ScanResult?)
    fun onBatchScanResults(results: List<ScanResult?>?)
    fun onScanFailed(errorCode: Int)
}
