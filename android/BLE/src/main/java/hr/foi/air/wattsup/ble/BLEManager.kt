package hr.foi.air.wattsup.ble

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import hr.foi.air.wattsup.core.CardScanCallback

class BLEManager(
    private val context: Context,
    private val permissionCallback: PermissionCallback? = null,
) {

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
    )

    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    private var bluetoothGatt: BluetoothGatt? = null

    private var scanCallback: ScanCallback? = null

    init {
        initializeBluetooth()
    }

    companion object {
        private const val REQUEST_PERMISSIONS = 1
        private const val REQUEST_PERMISSIONS_CONNECT = 2
        private const val REQUEST_PERMISSIONS_SCAN = 3
        private const val REQUEST_ENABLE_BLUETOOTH = 4
    }

    fun isBluetoothSupported(): Boolean = bluetoothAdapter != null
    fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true

    private fun initializeBluetooth() {
        if (!isBluetoothSupported()) {
            val message = "Bluetooth not supported on this device"
            Log.i("BLUETOOTH", message)
            return
        }

        if (!isBluetoothEnabled()) {
            val message = "Not enabled - please enable Bluetooth in Settings on your device"
            Log.i("BLUETOOTH", message)
            return
        }

        Log.i("BLUETOOTH", "Supported and enabled")
        return
    }

    private fun requestBluetoothPermissions(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionsToRequest = REQUIRED_PERMISSIONS.filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }
            if (permissionsToRequest.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    permissionsToRequest.toTypedArray(),
                    REQUEST_PERMISSIONS,
                )
            }
        }
    }

    fun startScanning(scanCallback: ScanCallback, bleScanCallback: CardScanCallback?) {
        this.scanCallback = scanCallback

        val scanFilters = mutableListOf<ScanFilter>()
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        checkAndRequestPermission(
            Manifest.permission.BLUETOOTH_SCAN,
            REQUEST_PERMISSIONS_SCAN,
        )
        bluetoothLeScanner?.startScan(scanFilters, scanSettings, scanCallback)
        bleScanCallback?.onScanStarted()
    }

    fun stopScanning() {
        checkAndRequestPermission(
            Manifest.permission.BLUETOOTH_SCAN,
            REQUEST_PERMISSIONS_SCAN,
        )
        if (scanCallback != null) {
            bluetoothLeScanner?.stopScan(scanCallback)
            scanCallback = null
        }
    }

    fun connectToDevice(device: BluetoothDevice, gattCallback: BluetoothGattCallback) {
        checkAndRequestPermission(
            Manifest.permission.BLUETOOTH_CONNECT,
            REQUEST_PERMISSIONS_CONNECT,
        )
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    fun disconnectDevice() {
        checkAndRequestPermission(
            Manifest.permission.BLUETOOTH_CONNECT,
            REQUEST_PERMISSIONS_CONNECT,
        )
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
    }

    fun sendData(characteristic: BluetoothGattCharacteristic, data: ByteArray) {
        characteristic.value = data
        checkAndRequestPermission(
            Manifest.permission.BLUETOOTH_CONNECT,
            REQUEST_PERMISSIONS_CONNECT,
        )
        bluetoothGatt?.writeCharacteristic(characteristic)
    }

    fun showEnableBluetoothOption(context: Context) {
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
        } else {
            // Permission is already granted
            permissionCallback?.onPermissionGranted(permission)
        }
    }
}
