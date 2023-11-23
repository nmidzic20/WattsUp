package hr.foi.air.wattsup.rfid

interface PermissionCallbackRFID {
    fun onPermissionGranted(permission: String)
    fun onPermissionDenied(permission: String)
}
