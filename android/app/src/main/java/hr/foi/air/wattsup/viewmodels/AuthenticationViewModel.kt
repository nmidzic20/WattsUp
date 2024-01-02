package hr.foi.air.wattsup.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.LoginBody
import hr.foi.air.wattsup.network.models.LoginResponseBody
import hr.foi.air.wattsup.network.models.RegistrationBody
import hr.foi.air.wattsup.network.models.RegistrationResponseBody
import hr.foi.air.wattsup.network.models.TokenManager
import hr.foi.air.wattsup.utils.LastAddedCard
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

    // registration
    private val _firstName = MutableLiveData<String>("")
    val firstName: LiveData<String> = _firstName

    private val _lastName = MutableLiveData<String>("")
    val lastName: LiveData<String> = _lastName

    private val _card = MutableLiveData<Card?>(null)
    val card: LiveData<Card?> = _card

    private val _invalidEmail = MutableLiveData<Boolean>(false)
    val invalidEmail: LiveData<Boolean> = _invalidEmail

    private val _invalidPassword = MutableLiveData<Boolean>(false)
    val invalidPassword: LiveData<Boolean> = _invalidPassword

    private val _passwordVisible = MutableLiveData<Boolean>(false)
    val passwordVisible: LiveData<Boolean> = _passwordVisible

    private val lastAddedCardObserver = Observer<Card> { newCard ->
        updateCard(newCard)
    }

    init {
        // Update card variable if LastAddedCard.userCard has a new card added
        LastAddedCard.userCard.observeForever(lastAddedCardObserver)
    }

    override fun onCleared() {
        super.onCleared()
        LastAddedCard.userCard.removeObserver(lastAddedCardObserver)
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value!!
    }

    fun updateEmail(email: String) {
        _email.value = email
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun updateFirstName(value: String) {
        _firstName.value = value
    }

    fun updateLastName(value: String) {
        _lastName.value = value
    }

    fun updateCard(value: Card) {
        _card.value = value
    }

    fun updateInvalidEmail(value: Boolean) {
        _invalidEmail.value = value
    }

    fun updateInvalidPassword(value: Boolean) {
        _invalidPassword.value = value
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

    fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        card: Card?,
        context: Context,
        onLogInClick: () -> Unit,
    ) {
        _showLoading.value = true

        authService.registerUser(
            RegistrationBody(firstName, lastName, email, password, card),
        ).enqueue(
            object : Callback<RegistrationResponseBody> {
                override fun onResponse(
                    call: Call<RegistrationResponseBody>?,
                    response: Response<RegistrationResponseBody>?,
                ) {
                    Log.i("RES", response.toString())
                    _showLoading.value = false

                    if (response?.isSuccessful == true) {
                        val responseBody = response.body()
                        val message =
                            "Registered new user under ID ${responseBody?.id}, first name: ${responseBody?.firstName}, last name: ${responseBody?.lastName}, email: ${responseBody?.email}"
                        Log.i("Response", message)
                        showToast(context, "Successfully registered user")
                        onLogInClick()
                    } else {
                        val responseBody = response?.body()
                        Log.i("Response", (responseBody ?: response).toString())
                        showToast(context, "Failed to register user")
                        onLogInClick()
                    }
                }

                override fun onFailure(
                    call: Call<RegistrationResponseBody>?,
                    t: Throwable?,
                ) {
                    Log.i("Response", t.toString())
                    showToast(context, "Failed to register user")
                }
            },
        )
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
