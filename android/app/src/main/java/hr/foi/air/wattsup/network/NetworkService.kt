package hr.foi.air.wattsup.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object NetworkService {
    private const val BASE_URL = "https://192.168.18.18:32770/"

    /*private var instance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthenticationService = instance.create(AuthenticationService::class.java)*/
    private val instance: Retrofit by lazy {
        val trustManager: X509TrustManager = object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<out java.security.cert.X509Certificate>?,
                authType: String?,
            ) {
            }

            override fun checkServerTrusted(
                chain: Array<out java.security.cert.X509Certificate>?,
                authType: String?,
            ) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return emptyArray()
            }
        }

        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), java.security.SecureRandom())

        val client: OkHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier { _, _ -> true }
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val authService: AuthenticationService by lazy { instance.create(AuthenticationService::class.java) }
    val cardService: CardService by lazy { instance.create(CardService::class.java) }
    val eventService: EventService by lazy { instance.create(EventService::class.java) }
}
