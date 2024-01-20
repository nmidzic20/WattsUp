package hr.foi.air.wattsup.repository

import android.content.Context
import hr.foi.air.wattsup.network.models.LoginBody
import hr.foi.air.wattsup.network.models.LoginResponseBody
import hr.foi.air.wattsup.network.models.RegistrationBody
import hr.foi.air.wattsup.network.models.RegistrationResponseBody

interface WattsUpRepository {
    fun loginUser(
        context: Context,
        loginBody: LoginBody,
        onLogin: (LoginResponseBody) -> Unit,
        onError: (String) -> Unit,
    )

    fun registerUser(
        registrationBody: RegistrationBody,
        onSuccess: (RegistrationResponseBody) -> Unit,
        onError: (String) -> Unit,
    )
}
