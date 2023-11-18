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
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat

class BLEManager(private val context: Context) {

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

    init {
        initializeBluetooth()
    }

    companion object {
        private const val REQUEST_PERMISSIONS = 1
        private const val REQUEST_PERMISSIONS_CONNECT = 2
        private const val REQUEST_PERMISSIONS_SCAN = 3
    }

    private fun isBluetoothSupported(): Boolean = bluetoothAdapter != null
    private fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true

    private fun initializeBluetooth() {
        if (!isBluetoothSupported()) {
            Log.i("BLUETOOTH", "Not supported on this device")
            return
        }

        if (!isBluetoothEnabled()) {
            Log.i("BLUETOOTH", "Not enabled on this device - launch settings for user to enable")
            return
        }

        Log.i("BLUETOOTH", "Supported and enabled")
    }

    fun requestBluetoothPermissions(context: Context) {
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

    fun startScanning(scanCallback: ScanCallback) {
        val scanFilters = mutableListOf<ScanFilter>()
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        checkAndRequestPermission(
            Manifest.permission.BLUETOOTH_SCAN,
            REQUEST_PERMISSIONS_SCAN,
        )
        bluetoothLeScanner?.startScan(scanFilters, scanSettings, scanCallback)
    }

    fun stopScanning(scanCallback: ScanCallback) {
        checkAndRequestPermission(
            Manifest.permission.BLUETOOTH_SCAN,
            REQUEST_PERMISSIONS_SCAN,
        )
        bluetoothLeScanner?.stopScan(scanCallback)
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

    /*
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            REQUEST_PERMISSIONS -> {

            }

            REQUEST_PERMISSIONS_CONNECT -> {

            }

            REQUEST_PERMISSIONS_SCAN -> {

            }

            //other cases - call super method

        }
    }
    */
}
