package hr.foi.air.wattsup.repository

import android.content.Context
import android.util.Log
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.CardPOSTBody
import hr.foi.air.wattsup.network.models.Charger
import hr.foi.air.wattsup.network.models.Event
import hr.foi.air.wattsup.network.models.LoginBody
import hr.foi.air.wattsup.network.models.LoginResponseBody
import hr.foi.air.wattsup.network.models.RegistrationBody
import hr.foi.air.wattsup.network.models.RegistrationResponseBody
import hr.foi.air.wattsup.network.models.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WattsUpRepositoryImpl : WattsUpRepository {
    private val authService = NetworkService.authService
    private val cardService = NetworkService.cardService

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

    override fun getCardsForUser(
        userId: Int,
        authToken: String,
        onSuccess: (response: Response<List<Card?>>) -> Unit,
        onFailure: (message: String) -> Unit,
        onExpiredToken: () -> Unit,
    ) {
        cardService.getCardsForUser(userId, authToken).enqueue(object : Callback<List<Card?>> {
            override fun onResponse(call: Call<List<Card?>>, response: Response<List<Card?>>) {
                if (response.isSuccessful) {
                    onSuccess(response)
                } else {
                    if (response.code() == 401) {
                        onExpiredToken()
                    } else {
                        onFailure(response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<List<Card?>>, t: Throwable) {
                onFailure(t.toString())
            }
        })
    }

    override fun addCard(
        userId: Int,
        cardValue: String,
        authToken: String,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        cardService.addCard(CardPOSTBody(userId, cardValue), authToken).enqueue(
            object : Callback<Card> {
                override fun onResponse(
                    call: Call<Card>,
                    response: Response<Card>,
                ) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else if (response.code() == 409) {
                        onFailure("Card already exists!")
                    } else {
                        onFailure("Error adding card!")
                    }
                }

                override fun onFailure(call: Call<Card>, t: Throwable) {
                    onFailure(t.toString())
                }
            },
        )
    }

    override fun deleteCard(
        cardId: Int,
        authToken: String,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit,
    ) {
        cardService.deleteCard(cardId, authToken).enqueue(
            object : Callback<Card> {
                override fun onResponse(
                    call: Call<Card>,
                    response: Response<Card>,
                ) {
                    Log.i("CardView", response.toString())
                    if (response.isSuccessful) {
                        onSuccess()
                    } else if (response.code() == 409) {
                        onFailure("Card not found!")
                    } else {
                        onFailure("Error deleting card!")
                    }
                }

                override fun onFailure(call: Call<Card>, t: Throwable) {
                    onFailure(t.toString())
                }
            },
        )
    }

    override suspend fun getChargerName(context: Context, chargerId: Int): String? {
        val chargerService = NetworkService.chargerService
        val auth = "Bearer " + TokenManager.getInstance(context).getJWTToken()

        return suspendCoroutine { continuation ->
            chargerService.getCharger(chargerId, auth)
                .enqueue(object : Callback<Charger?> {
                    override fun onResponse(call: Call<Charger?>, response: Response<Charger?>) {
                        if (response.isSuccessful) {
                            continuation.resume(response.body()!!.name)
                            Log.d("HistoryScreen", "Cards: ${response.body()}")
                        } else {
                            Log.d("HistoryScreen", "Error: ${response.errorBody()}")
                            continuation.resume(null)
                        }
                    }

                    override fun onFailure(call: Call<Charger?>, t: Throwable) {
                        Log.d("HistoryScreen", "Failure: ${t.message}")
                        continuation.resume(null)
                    }
                })
        }
    }

    override suspend fun getCards(context: Context, userId: Int): List<Card?> {
        val cardService = NetworkService.cardService
        val auth = "Bearer " + TokenManager.getInstance(context).getJWTToken()

        return suspendCoroutine { continuation ->
            cardService.getCardsForUser(userId, auth)
                .enqueue(object : Callback<List<Card?>> {
                    override fun onResponse(
                        call: Call<List<Card?>>,
                        response: Response<List<Card?>>,
                    ) {
                        if (response.isSuccessful) {
                            continuation.resume(response.body() ?: emptyList())
                            Log.d("HistoryScreen", "Cards: ${response.body()}")
                        } else {
                            Log.d("HistoryScreen", "Error: ${response.errorBody()}")
                            continuation.resume(emptyList())
                        }
                    }

                    override fun onFailure(call: Call<List<Card?>>, t: Throwable) {
                        Log.d("HistoryScreen", "Failure: ${t.message}")
                        continuation.resume(emptyList())
                    }
                })
        }
    }

    override suspend fun getEvents(
        context: Context,
        cardId: Int,
        onResponse: (String) -> Unit,
    ): List<Event?> {
        val eventService = NetworkService.eventService
        val auth = "Bearer " + TokenManager.getInstance(context).getJWTToken()

        return suspendCoroutine { continuation ->
            eventService.getEvents(cardId, auth).enqueue(object : Callback<List<Event?>> {
                override fun onResponse(
                    call: Call<List<Event?>>,
                    response: Response<List<Event?>>,
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body() ?: emptyList())
                        Log.d("HistoryScreen", "Events: ${response.body()}")
                    } else {
                        Log.d("HistoryScreen", "Error: ${response.errorBody()}")
                        onResponse("Error: ${response.errorBody()}")
                        continuation.resume(emptyList())
                    }
                }

                override fun onFailure(call: Call<List<Event?>>, t: Throwable) {
                    Log.d("HistoryScreen", "Failure: ${t.message}")
                    onResponse("Failure: ${t.message}")
                    continuation.resume(emptyList())
                }
            })
        }
    }

    override fun authenticateCard(
        deviceAddress: String,
        onResponse: () -> Unit,
        onCardAuthenticated: (card: Card) -> Unit,
        onCardInvalid: () -> Unit,
    ) {
        cardService.authenticateCard(deviceAddress).enqueue(object : Callback<Card?> {
            override fun onResponse(call: Call<Card?>, response: Response<Card?>) {
                onResponse()

                if (response.isSuccessful && response.body() != null) {
                    onCardAuthenticated(response.body()!!)
                    // put onCardInvalid() instead of onCardAuthenticated to test scanning with invalid card
                } else {
                    onCardInvalid()
                }
            }

            override fun onFailure(call: Call<Card?>, t: Throwable) {
                onCardInvalid()
            }
        })
    }
}
