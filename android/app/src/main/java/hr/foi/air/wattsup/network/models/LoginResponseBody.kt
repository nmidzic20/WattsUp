package hr.foi.air.wattsup.network.models

class LoginResponseBody(val jwt:String,val refreshToken:String,val refreshTokenExpiresAt:String)