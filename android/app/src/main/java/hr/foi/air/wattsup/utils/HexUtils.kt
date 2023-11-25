package hr.foi.air.wattsup.utils

object HexUtils {
    fun formatHexToPrefix(hexString: String): String {
        val parts = hexString.split(":")
        val formattedHex = parts.joinToString("") { part ->
            // Convert each part to a two-character lowercase hex string
            String.format("%02x", part.toInt(16))
        }
        return "0x$formattedHex"
    }

    fun compareHexStrings(hexString1: String, hexString2: String): Boolean =
        hexString1.equals(hexString2, ignoreCase = true)
}
