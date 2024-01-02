package hr.foi.air.wattsup.network

import hr.foi.air.wattsup.network.models.Charger
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ChargerService {
    @GET("api/Charger/{chargerId}")
    fun getCharger(
        @Path("chargerId") chargerId: Int,
        @Header("Authorization") jwt: String,
    ): Call<Charger?>

    @GET("api/Charger")
    fun getChargers(): Call<List<Charger?>>
}
