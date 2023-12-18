package hr.foi.air.wattsup.network

import hr.foi.air.wattsup.network.models.Charger
import hr.foi.air.wattsup.network.models.Event
import hr.foi.air.wattsup.network.models.EventPOSTBody
import hr.foi.air.wattsup.network.models.EventPOSTResponseBody
import hr.foi.air.wattsup.network.models.EventPUTBody
import hr.foi.air.wattsup.network.models.EventPUTResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ChargerService {
    @GET("api/Charger/{chargerId}")
    fun getCharger(@Path("chargerId") chargerId: Int, @Header("Authorization") jwt: String): Call<Charger?>
}