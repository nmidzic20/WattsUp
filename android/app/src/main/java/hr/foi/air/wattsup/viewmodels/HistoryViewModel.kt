package hr.foi.air.wattsup.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.Charger
import hr.foi.air.wattsup.network.models.Event
import hr.foi.air.wattsup.network.models.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HistoryViewModel : ViewModel() {
    private val _cards = MutableLiveData<List<Card?>>()

    private val _events = MutableLiveData<List<Event?>>()
    val events: LiveData<List<Event?>> = _events

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    suspend fun refreshHistory(context: Context, userId: Int) {
        _showLoading.value = true
        _events.value = emptyList()
        fetchChargingHistory(context, userId)
    }

    fun resetEvents() {
        _events.value = emptyList()
    }

    private suspend fun fetchChargingHistory(context: Context, userId: Int) {
        _cards.value = getCards(context, userId)
        for (card in _cards.value!!) {
            val data = getEvents(context, card!!.id)
            for (event in data) {
                event!!.cardValue = card.value
                event.chargerLocation = getChargerName(context, event.chargerId) ?: ""
            }
            _events.value = data
        }
        _events.value = _events.value?.sortedByDescending { it!!.startedAt }
        _showLoading.value = false
    }

    private suspend fun getEvents(context: Context, cardId: Int): List<Event?> {
        val eventService = NetworkService.eventService
        val auth = "Bearer " + TokenManager.getInstance(context).getJWTToken()

        return suspendCoroutine { continuation ->
            eventService.getEvents(cardId, auth).enqueue(object : Callback<List<Event?>> {
                override fun onResponse(
                    call: Call<List<Event?>>,
                    response: Response<List<Event?>>,
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body() ?: emptyList())
                        Log.d("HistoryScreen", "Events: ${response.body()}")
                    } else {
                        Log.d("HistoryScreen", "Error: ${response.errorBody()}")
                        toast(context, "Error: ${response.errorBody()}")
                        continuation.resume(emptyList())
                    }
                }

                override fun onFailure(call: Call<List<Event?>>, t: Throwable) {
                    Log.d("HistoryScreen", "Failure: ${t.message}")
                    toast(context, "Failure: ${t.message}")
                    continuation.resume(emptyList())
                }
            })
        }
    }

    private suspend fun getCards(context: Context, userId: Int): List<Card?> {
        val cardService = NetworkService.cardService
        val auth = "Bearer " + TokenManager.getInstance(context).getJWTToken()

        return suspendCoroutine { continuation ->
            cardService.getCardsForUser(userId, auth)
                .enqueue(object : Callback<List<Card?>> {
                    override fun onResponse(
                        call: Call<List<Card?>>,
                        response: Response<List<Card?>>,
                    ) {
                        if (response.isSuccessful) {
                            continuation.resume(response.body() ?: emptyList())
                            Log.d("HistoryScreen", "Cards: ${response.body()}")
                        } else {
                            Log.d("HistoryScreen", "Error: ${response.errorBody()}")
                            continuation.resume(emptyList())
                        }
                    }

                    override fun onFailure(call: Call<List<Card?>>, t: Throwable) {
                        Log.d("HistoryScreen", "Failure: ${t.message}")
                        continuation.resume(emptyList())
                    }
                })
        }
    }

    private suspend fun getChargerName(context: Context, chargerId: Int): String? {
        val chargerService = NetworkService.chargerService
        val auth = "Bearer " + TokenManager.getInstance(context).getJWTToken()

        return suspendCoroutine { continuation ->
            chargerService.getCharger(chargerId, auth)
                .enqueue(object : Callback<Charger?> {
                    override fun onResponse(call: Call<Charger?>, response: Response<Charger?>) {
                        if (response.isSuccessful) {
                            continuation.resume(response.body()!!.name)
                            Log.d("HistoryScreen", "Cards: ${response.body()}")
                        } else {
                            Log.d("HistoryScreen", "Error: ${response.errorBody()}")
                            continuation.resume(null)
                        }
                    }

                    override fun onFailure(call: Call<Charger?>, t: Throwable) {
                        Log.d("HistoryScreen", "Failure: ${t.message}")
                        continuation.resume(null)
                    }
                })
        }
    }

    private fun toast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
