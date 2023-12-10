package hr.foi.air.wattsup.ble

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import hr.foi.air.wattsup.core.CardManager
import hr.foi.air.wattsup.core.CardScanCallback

class BLEManager(
    private val context: Context,
) : CardManager {

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
    )

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var bluetoothGatt: BluetoothGatt? = null

    private var scanCallback: ScanCallback? = null
    private var onScanStop: () -> Unit = {}

    init {
        initialize()
        // registerBluetoothStateReceiver()
    }

    companion object {
        private const val REQUEST_PERMISSIONS = 1
        private const val REQUEST_PERMISSIONS_CONNECT = 2
        private const val REQUEST_PERMISSIONS_SCAN = 3
        private const val REQUEST_ENABLE_BLUETOOTH = 4
    }

    private fun initializeBluetoothComponents() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
    }

    /*private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED == intent?.action) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                handleBluetoothState(state)
            }
        }
    }

    private fun registerBluetoothStateReceiver() {
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(bluetoothStateReceiver, filter)
    }*/

    private fun handleBluetoothState(state: Int) {
        when (state) {
            BluetoothAdapter.STATE_ON -> {
                Log.i("SCAN_CARD", "Bluetooth is ON")
                initializeBluetoothComponents()
            }

            BluetoothAdapter.STATE_OFF -> {
                Log.i("SCAN_CARD", "Bluetooth is OFF")
                bluetoothAdapter = null
                bluetoothLeScanner = null
            }
            // Handle other Bluetooth states if needed
        }
    }

    override fun getName(): String = "BLE"

    override fun isCardSupportAvailableOnDevice(): Boolean = bluetoothAdapter != null
    override fun isCardSupportEnabledOnDevice(): Boolean = bluetoothAdapter?.isEnabled == true

    override fun initialize() {
        initializeBluetoothComponents()

        if (!isCardSupportAvailableOnDevice()) {
            val message = "Bluetooth not supported on this device"
            Log.i("BLUETOOTH", message)
            return
        }

        if (!isCardSupportEnabledOnDevice()) {
            val message = "Not enabled - please enable Bluetooth"
            Log.i("BLUETOOTH", message)
            return
        }

        Log.i("BLUETOOTH", "Supported and enabled")
        return
    }

    override fun startScanningForCard(bleScanCallback: CardScanCallback?) {
        Log.i("SCAN_CARD", "Ble startscan")
        this.scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                Log.i("SCAN_CARD", result.device.address)
                bleScanCallback?.onScanResult(result.device.address)
            }

            override fun onScanFailed(errorCode: Int) {
                Log.i("SCAN_CARD", "Error $errorCode")
                bleScanCallback?.onScanFailed(errorCode.toString())
            }
        }

        this.onScanStop = {
            Log.i("SCAN_CARD", "Stopped")
            bleScanCallback?.onScanStopped()
        }

        val scanFilters = mutableListOf<ScanFilter>()
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        checkAndRequestPermission(
            Manifest.permission.BLUETOOTH_SCAN,
            REQUEST_PERMISSIONS_SCAN,
        )
        bluetoothLeScanner?.startScan(scanFilters, scanSettings, this.scanCallback)
        bleScanCallback?.onScanStarted()
    }

    override fun stopScanningForCard() {
        checkAndRequestPermission(
            Manifest.permission.BLUETOOTH_SCAN,
            REQUEST_PERMISSIONS_SCAN,
        )
        this.onScanStop()
        if (scanCallback != null) {
            bluetoothLeScanner?.flushPendingScanResults(scanCallback)
            bluetoothLeScanner?.stopScan(scanCallback)
            scanCallback = null
        }
    }

    override fun showEnableCardSupportOption(context: Context) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }
        (context as? Activity)?.startActivityForResult(
            enableBtIntent,
            REQUEST_ENABLE_BLUETOOTH,
        )
    }

    private fun checkAndRequestPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission if it is missing
            if (context is ComponentActivity) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(permission),
                    requestCode,
                )
            }
            return
        }
    }
}
