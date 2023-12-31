package hr.foi.air.wattsup.network.models

import android.content.Context
import android.content.SharedPreferences
import java.util.Base64

class TokenManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("TokenStorage", Context.MODE_PRIVATE)

    fun setjWTtoken(_jWTtoken: String) {
        with(sharedPreferences.edit()) {
            putString("jwtToken", _jWTtoken)
            apply()
        }
    }

    fun getjWTtoken(): String? {
        return sharedPreferences.getString("jwtToken", null)
    }

    fun setrefreshToken(_refreshToken: String) {
        with(sharedPreferences.edit()) {
            putString("refreshToken", _refreshToken)
            apply()
        }
    }

    fun getrefreshToken(): String? {
        return sharedPreferences.getString("refreshToken", null)
    }

    fun setrefreshTokenExpiresAt(_refreshTokenExpiresAt: String) {
        with(sharedPreferences.edit()) {
            putString("refreshTokenExpiresAt", _refreshTokenExpiresAt)
            apply()
        }
    }

    fun getrefreshTokenExpiresAt(): String? {
        return sharedPreferences.getString("refreshTokenExpiresAt", null)
    }

    fun getId(): Int {
        val payload = String(Base64.getUrlDecoder().decode(getjWTtoken()!!.split(".")[1]))
        return payload.split(",")[0].split(":")[1].replace("\"", "").toInt()
    }

    companion object {
        @Volatile
        private var instance: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return instance ?: synchronized(this) {
                instance ?: TokenManager(context).also { instance = it }
            }
        }
    }
}
