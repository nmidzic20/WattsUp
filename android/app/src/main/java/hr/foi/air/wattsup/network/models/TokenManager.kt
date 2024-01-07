package hr.foi.air.wattsup.network.models

import android.content.Context
import android.content.SharedPreferences
import java.util.Base64

class TokenManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("TokenStorage", Context.MODE_PRIVATE)

    fun setJWTToken(_jWTtoken: String) {
        with(sharedPreferences.edit()) {
            putString("jwtToken", _jWTtoken)
            apply()
        }
    }

    fun getJWTToken(): String? {
        return sharedPreferences.getString("jwtToken", null)
    }

    fun setRefreshToken(_refreshToken: String) {
        with(sharedPreferences.edit()) {
            putString("refreshToken", _refreshToken)
            apply()
        }
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refreshToken", null)
    }

    fun setRefreshTokenExpiresAt(_refreshTokenExpiresAt: String) {
        with(sharedPreferences.edit()) {
            putString("refreshTokenExpiresAt", _refreshTokenExpiresAt)
            apply()
        }
    }

    fun getRefreshTokenExpiresAt(): String? {
        return sharedPreferences.getString("refreshTokenExpiresAt", null)
    }

    fun getUserId(): Int {
        val payload = String(Base64.getUrlDecoder().decode(getJWTToken()!!.split(".")[1]))
        return payload.split(",")[0].split(":")[1].replace("\"", "").toInt()
    }

    fun isLoggedIn(): Boolean = !(getJWTToken().isNullOrEmpty())

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
