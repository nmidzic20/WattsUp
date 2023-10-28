package hr.foi.air.wattsup.network.models

data class RegistrationBody(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
)
