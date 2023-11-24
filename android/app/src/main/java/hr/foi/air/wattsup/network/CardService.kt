package hr.foi.air.wattsup.network

import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.LoginBody
import hr.foi.air.wattsup.network.models.RegistrationBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET

interface CardService {
    @GET("api/Card")
    fun getCard(@Body registerBody: RegistrationBody): Call<Card>

    @GET("api/Card/{userId}")
    fun getCardByUserId(@Body loginBody: LoginBody): Call<Card>
}
