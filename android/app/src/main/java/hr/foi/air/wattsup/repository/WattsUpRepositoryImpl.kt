package hr.foi.air.wattsup.repository

import android.content.Context
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.LoginBody
import hr.foi.air.wattsup.network.models.LoginResponseBody
import hr.foi.air.wattsup.network.models.RegistrationBody
import hr.foi.air.wattsup.network.models.RegistrationResponseBody
import hr.foi.air.wattsup.network.models.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WattsUpRepositoryImpl : WattsUpRepository {
    private val authService = NetworkService.authService

    override fun loginUser(
        context: Context,
        loginBody: LoginBody,
        onLogin: (LoginResponseBody) -> Unit,
        onError: (String) -> Unit,
    ) {
        authService.loginUser(loginBody).enqueue(object : Callback<LoginResponseBody> {
            override fun onResponse(
                call: Call<LoginResponseBody>,
                response: Response<LoginResponseBody>,
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val refreshToken = responseBody?.refreshToken ?: ""
                    val jwtToken = responseBody?.jwt ?: ""

                    TokenManager.getInstance(context).apply {
                        setRefreshToken(refreshToken)
                        setRefreshTokenExpiresAt(responseBody!!.refreshToken)
                        setJWTToken(jwtToken)
                    }

                    onLogin(responseBody!!)
                } else {
                    onError("Invalid e-mail or password")
                }
            }

            override fun onFailure(call: Call<LoginResponseBody>, t: Throwable) {
                onError("Failed to login user")
            }
        })
    }

    override fun registerUser(
        registrationBody: RegistrationBody,
        onSuccess: (RegistrationResponseBody) -> Unit,
        onError: (String) -> Unit,
    ) {
        authService.registerUser(registrationBody)
            .enqueue(object : Callback<RegistrationResponseBody> {
                override fun onResponse(
                    call: Call<RegistrationResponseBody>,
                    response: Response<RegistrationResponseBody>,
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        onSuccess(responseBody!!)
                    } else {
                        onError("Failed to register user")
                    }
                }

                override fun onFailure(call: Call<RegistrationResponseBody>, t: Throwable) {
                    onError("Failed to register user")
                }
            })
    }
}
