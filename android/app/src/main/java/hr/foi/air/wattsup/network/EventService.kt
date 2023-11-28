package hr.foi.air.wattsup.network

import hr.foi.air.wattsup.network.models.EventBody
import hr.foi.air.wattsup.network.models.EventResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface EventService {
    @POST("api/Event")
    fun logEvent(@Body eventBody: EventBody): Call<EventResponseBody>
}