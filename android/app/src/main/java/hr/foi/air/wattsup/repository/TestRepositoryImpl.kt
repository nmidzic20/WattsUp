package hr.foi.air.wattsup.repository

import android.content.Context
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.Event
import hr.foi.air.wattsup.network.models.LoginBody
import hr.foi.air.wattsup.network.models.LoginResponseBody
import hr.foi.air.wattsup.network.models.RegistrationBody
import hr.foi.air.wattsup.network.models.RegistrationResponseBody
import retrofit2.Response

class TestRepositoryImpl : WattsUpRepository {
    override fun loginUser(
        context: Context,
        loginBody: LoginBody,
        onLogin: (LoginResponseBody) -> Unit,
        onError: (String) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override fun registerUser(
        registrationBody: RegistrationBody,
        onSuccess: (RegistrationResponseBody) -> Unit,
        onError: (String) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override fun getCardsForUser(
        userId: Int,
        authToken: String,
        onSuccess: (response: Response<List<Card?>>) -> Unit,
        onFailure: (message: String) -> Unit,
        onExpiredToken: () -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override fun addCard(
        userId: Int,
        cardValue: String,
        authToken: String,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteCard(
        cardId: Int,
        authToken: String,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getChargerName(context: Context, chargerId: Int): String? {
        TODO("Not yet implemented")
    }

    override suspend fun getCards(context: Context, userId: Int): List<Card?> {
        TODO("Not yet implemented")
    }

    override suspend fun getEvents(
        context: Context,
        cardId: Int,
        onResponse: (String) -> Unit,
    ): List<Event?> {
        TODO("Not yet implemented")
    }

    override fun authenticateCard(
        deviceAddress: String,
        onResponse: () -> Unit,
        onCardAuthenticated: (card: Card) -> Unit,
        onCardInvalid: () -> Unit,
    ) {
        TODO("Not yet implemented")
    }
}
