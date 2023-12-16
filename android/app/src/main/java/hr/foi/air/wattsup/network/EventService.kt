package hr.foi.air.wattsup.network

import hr.foi.air.wattsup.network.models.EventPOSTBody
import hr.foi.air.wattsup.network.models.EventPOSTResponseBody
import hr.foi.air.wattsup.network.models.EventPUTBody
import hr.foi.air.wattsup.network.models.EventPUTResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface EventService {
    @POST("api/Event")
    fun logEventStart(@Body eventPOSTBody: EventPOSTBody): Call<EventPOSTResponseBody>

    @PUT("api/Event")
    fun logEventEnd(@Body eventPUTBody: EventPUTBody): Call<EventPUTResponseBody>
}
