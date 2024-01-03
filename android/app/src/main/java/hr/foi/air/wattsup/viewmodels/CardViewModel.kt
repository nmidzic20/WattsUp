package hr.foi.air.wattsup.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.CardPOSTBody
import hr.foi.air.wattsup.network.models.TokenManager
import hr.foi.air.wattsup.utils.LastAddedCard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CardViewModel : ViewModel() {
    private val cardService = NetworkService.cardService

    private val _cards = MutableLiveData<List<Card?>>()
    val cards: LiveData<List<Card?>> = _cards

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    private val _card = MutableLiveData<Card?>(null)
    val card: LiveData<Card?> = _card

    private val lastAddedCardObserver = Observer<Card> { newCard ->
        updateCard(newCard)
    }

    init {
        // Update card variable if LastAddedCard.userCard has a new card added
        LastAddedCard.userCard.observeForever(lastAddedCardObserver)
    }

    override fun onCleared() {
        super.onCleared()
        LastAddedCard.userCard.removeObserver(lastAddedCardObserver)
    }

    fun updateCard(value: Card?) {
        _card.value = value
    }

    suspend fun refreshCards(context: Context, userId: Int) {
        _showLoading.value = true
        _cards.value = emptyList()
        fetchCards(context, userId)
    }

    private suspend fun fetchCards(context: Context, userId: Int) {
        _cards.value = getCards(context, userId)
        _showLoading.value = false
    }

    private suspend fun getCards(context: Context, userId: Int): List<Card?> {
        val auth = "Bearer " + TokenManager.getInstance(context).getJWTToken()

        return suspendCoroutine { continuation ->
            cardService.getCardsForUser(userId, auth).enqueue(object : Callback<List<Card?>> {
                override fun onResponse(call: Call<List<Card?>>, response: Response<List<Card?>>) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body() ?: emptyList())
                        Log.d("CardView", "Cards: ${response.body()}")
                    } else {
                        Log.d("CardView", "Error: ${response.errorBody()}")
                        toast(context, "Error: ${response.errorBody()}")
                        continuation.resume(emptyList())
                    }
                }

                override fun onFailure(call: Call<List<Card?>>, t: Throwable) {
                    Log.d("CardView", "Failure: ${t.message}")
                    continuation.resume(emptyList())
                    toast(context, "Failure: ${t.message}")
                }
            })
        }
    }

    fun addCard(userId: Int, context: Context) {
        val auth = "Bearer " + TokenManager.getInstance(context).getJWTToken()

        cardService.addCard(CardPOSTBody(userId, card.value!!.value), auth).enqueue(
            object : Callback<Card> {
                override fun onResponse(
                    call: Call<Card>,
                    response: Response<Card>,
                ) {
                    Log.i("CardView", response.toString())

                    if (response.isSuccessful) {
                        toast(context, "Card added!")
                    } else if (response.code() == 409){
                        toast(context, "Card already exists!")
                    } else {
                        toast(context, "Error adding card!")
                    }
                }

                override fun onFailure(call: Call<Card>, t: Throwable) {
                    Log.i("CardView", t.toString())
                }
            },
        )

        updateCard(null)
    }

    fun deleteCard(cardId: Int, context: Context) {
        val auth = "Bearer " + TokenManager.getInstance(context).getJWTToken()

        cardService.deleteCard(cardId, auth).enqueue(
            object : Callback<Card> {
                override fun onResponse(
                    call: Call<Card>,
                    response: Response<Card>,
                ) {
                    Log.i("CardView", response.toString())
                    if (response.isSuccessful) {
                        toast(context, "Card deleted!")
                    } else if (response.code() == 409){
                        toast(context, "Card not found!")
                    } else {
                        toast(context, "Error deleting card!")
                    }
                }

                override fun onFailure(call: Call<Card>, t: Throwable) {
                    Log.i("CardView", t.toString())
                }
            },
        )
    }

    private fun toast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}