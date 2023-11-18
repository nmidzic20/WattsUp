package hr.foi.air.wattsup.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.util.Log

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
    }

    fun isBluetoothSupported(): Boolean {
        return bluetoothAdapter != null
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    fun initializeBluetooth() {
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
}
