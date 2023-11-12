package hr.foi.air.wattsup.network.models

data class RegistrationBody(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val card: RFIDCard?,
)

data class RFIDCard(
    val value: String,
)
