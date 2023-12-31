package hr.foi.air.wattsup.network

import hr.foi.air.wattsup.network.models.Card
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CardService {
    @GET("api/Card")
    fun getCards(): Call<List<Card?>>

    @GET("api/Card/CardAuthentication/{address}")
    fun authenticateCard(@Path("address") address: String): Call<Card?>

    @GET("api/Card/{userId}")
    fun getCardsForUser(@Path("userId") userId: Int, @Header("Authorization") jwt: String): Call<List<Card?>>
}
