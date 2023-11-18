package hr.foi.air.wattsup.ble

interface PermissionCallback {
    fun onPermissionGranted(permission: String)
    fun onPermissionDenied(permission: String)
}
