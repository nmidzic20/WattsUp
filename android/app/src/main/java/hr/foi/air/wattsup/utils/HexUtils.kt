package hr.foi.air.wattsup.utils

import android.util.Log

object HexUtils {
    fun formatHexToPrefix(hexString: String): String {
        val parts = hexString.removeSuffix(":").split(":")
        val formattedHex = parts.joinToString("") { part ->
            // Convert each part to a two-character lowercase hex string
            String.format("%02x", part.toInt(16))
        }
        return "0x${formattedHex.uppercase()}"
    }

    fun compareHexStrings(hexString1: String, hexString2: String): Boolean {
        Log.i(
            "CARD_MATCH?",
            "$hexString1 $hexString2 ${hexString1.equals(hexString2, ignoreCase = true)}",
        )
        return hexString1.equals(hexString2, ignoreCase = true)
    }

    fun bytesToHexString(bytes: ByteArray): String {
        val stringBuilder = StringBuilder()
        for (byte in bytes) {
            stringBuilder.append(String.format("%02X", byte))
            stringBuilder.append(":")
        }
        return stringBuilder.toString()
    }

    fun formatToDatabase(deviceAddress: String) = "0" +
        deviceAddress.substring(1, 2).lowercase() + deviceAddress.uppercase()
            .substring(2)
}
