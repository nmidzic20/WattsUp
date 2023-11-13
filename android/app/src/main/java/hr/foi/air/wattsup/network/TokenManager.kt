package hr.foi.air.wattsup.network

class TokenManager(
    private var jWTtoken: String,
    private var refreshToken: String,
    private var refreshTokenExpiresAt: String,
)
