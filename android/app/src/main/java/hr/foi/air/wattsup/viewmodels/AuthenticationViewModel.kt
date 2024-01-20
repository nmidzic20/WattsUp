package hr.foi.air.wattsup.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.LoginBody
import hr.foi.air.wattsup.network.models.RegistrationBody
import hr.foi.air.wattsup.repository.WattsUpRepositoryImpl
import hr.foi.air.wattsup.utils.LastRegisteredCard

class AuthenticationViewModel(private val repository: WattsUpRepositoryImpl) : ViewModel() {

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

    private val lastRegisteredCardObserver = Observer<Card> { newCard ->
        updateCard(newCard)
    }

    init {
        // Update card variable if LastRegisteredCard.userCard has a new card added
        LastRegisteredCard.userCard.observeForever(lastRegisteredCardObserver)
    }

    override fun onCleared() {
        super.onCleared()
        LastRegisteredCard.userCard.removeObserver(lastRegisteredCardObserver)
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

        repository.loginUser(
            context,
            LoginBody(email, password),
            onLogin = {
                _showLoading.value = false
                showToast(context, "Successfully logged in")
                onLogin()
            },
            onError = { errorMessage ->
                _showLoading.value = false
                _toastMessage.value = errorMessage
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

        repository.registerUser(
            RegistrationBody(firstName, lastName, email, password, card),
            onSuccess = {
                _showLoading.value = false
                showToast(context, "Successfully registered user")
                onLogInClick()
            },
            onError = { errorMessage ->
                _showLoading.value = false
                showToast(context, errorMessage)
                onLogInClick()
            },
        )
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
