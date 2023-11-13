package hr.foi.air.wattsup.network

class TokenManager {
    private var jWTtoken:String = ""
    private var refreshToken:String=""
    private var refreshTokenExpiresAt:String=""

    constructor(jWTtoken:String, refreshToken:String,refreshTokenExpiresAt:String){
        this.jWTtoken = jWTtoken
        this.refreshToken = refreshToken
        this.refreshTokenExpiresAt = refreshTokenExpiresAt
    }


}