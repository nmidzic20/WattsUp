package hr.foi.air.wattsup.network

import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.CardPOSTBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CardService {
    @GET("api/Card")
    fun getCards(): Call<List<Card?>>

    @GET("api/Card/CardAuthentication/{address}")
    fun authenticateCard(@Path("address") address: String): Call<Card?>

    @GET("api/Card/{userId}")
    fun getCardsForUser(@Path("userId") userId: Int, @Header("Authorization") jwt: String): Call<List<Card?>>

    @POST("api/Card")
    fun addCard(@Body card: CardPOSTBody, @Header("Authorization") jwt: String): Call<Card>

    @DELETE("api/Card/{cardId}")
    fun deleteCard(@Path("cardId") cardId: Int, @Header("Authorization") jwt: String): Call<Card>
}
