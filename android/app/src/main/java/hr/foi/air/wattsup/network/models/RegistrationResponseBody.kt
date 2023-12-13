package hr.foi.air.wattsup.network.models

data class RegistrationResponseBody(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
)
