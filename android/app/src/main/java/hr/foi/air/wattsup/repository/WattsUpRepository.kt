package hr.foi.air.wattsup.repository

import android.content.Context
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.Event
import hr.foi.air.wattsup.network.models.LoginBody
import hr.foi.air.wattsup.network.models.LoginResponseBody
import hr.foi.air.wattsup.network.models.RegistrationBody
import hr.foi.air.wattsup.network.models.RegistrationResponseBody
import retrofit2.Response

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

    fun getCardsForUser(
        userId: Int,
        authToken: String,
        onSuccess: (response: Response<List<Card?>>) -> Unit,
        onFailure: (message: String) -> Unit,
        onExpiredToken: () -> Unit,
    )

    fun addCard(
        userId: Int,
        cardValue: String,
        authToken: String,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit,
    )

    fun deleteCard(
        cardId: Int,
        authToken: String,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit,
    )

    suspend fun getChargerName(context: Context, chargerId: Int): String?
    suspend fun getCards(context: Context, userId: Int): List<Card?>
    suspend fun getEvents(
        context: Context,
        cardId: Int,
        onResponse: (String) -> Unit,
    ): List<Event?>
}
