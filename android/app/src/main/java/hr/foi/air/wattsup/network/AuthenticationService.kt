package hr.foi.air.wattsup.network

import hr.foi.air.wattsup.network.models.RegistrationBody
import hr.foi.air.wattsup.network.models.RegistrationResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationService {
    @POST("api/Users")
    fun registerUser(@Body registerBody: RegistrationBody): Call<RegistrationResponseBody>
}
