package hr.foi.air.wattsup.viewmodels

import android.content.Context
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.LoginBody
import hr.foi.air.wattsup.network.models.LoginResponseBody
import hr.foi.air.wattsup.network.models.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthenticationViewModel : ViewModel() {

    private val authService = NetworkService.authService

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading

    private val _toastMessage = MutableLiveData<String>("")
    val toastMessage: LiveData<String> = _toastMessage

    private val _interactionSource =
        MutableLiveData<MutableInteractionSource>(MutableInteractionSource())
    val interactionSource: LiveData<MutableInteractionSource> = _interactionSource

    private val _email = MutableLiveData<String>("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>("")
    val password: LiveData<String> = _password

    fun updateEmail(email: String) {
        _email.value = email
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun loginUser(email: String, password: String, context: Context, onLogin: () -> Unit) {
        _showLoading.value = true

        authService.loginUser(LoginBody(email, password)).enqueue(
            object : Callback<LoginResponseBody> {
                override fun onResponse(
                    call: Call<LoginResponseBody>?,
                    response: Response<LoginResponseBody>?,
                ) {
                    _showLoading.value = false

                    if (response?.isSuccessful == true) {
                        val responseBody = response.body()
                        val tokenManager = TokenManager.getInstance(context)
                        tokenManager.setRefreshToken(responseBody!!.refreshToken)
                        tokenManager.setRefreshTokenExpiresAt(responseBody.refreshToken)
                        tokenManager.setJWTToken(responseBody.jwt)
                        onLogin()
                    } else {
                        _toastMessage.value = "Invalid e-mail or password"
                    }
                }

                override fun onFailure(call: Call<LoginResponseBody>?, t: Throwable?) {
                    _showLoading.value = false
                    _toastMessage.value = "Failed to login user"
                }
            },
        )
    }
}
