package hr.foi.air.wattsup.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.TokenManager
import hr.foi.air.wattsup.repository.WattsUpRepository
import hr.foi.air.wattsup.utils.LastAddedCard
import retrofit2.Response

class CardViewModel(private val repository: WattsUpRepository) : ViewModel() {

    private val _cards = MutableLiveData<List<Card?>>()
    val cards: LiveData<List<Card?>> = _cards

    private val _showLoading = MutableLiveData<Boolean>(true)
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

    fun refreshCards(context: Context, userId: Int, onExpiredToken: () -> Unit) {
        _showLoading.value = true
        _cards.value = emptyList()

        val onSuccess = { response: Response<List<Card?>> ->
            _cards.value = response.body() ?: emptyList()
            _showLoading.value = false
        }

        val onFailure = { message: String ->
            toast(context, "Error: $message")
            _cards.value = emptyList()
            _showLoading.value = false
        }

        try {
            val authToken = "Bearer " + TokenManager.getInstance(context).getJWTToken()
            repository.getCardsForUser(userId, authToken, onSuccess, onFailure) {
                onExpiredToken()
                toast(context, "Token expired, please log in again")
            }
        } catch (e: Exception) {
            onExpiredToken()
        }
    }

    fun resetCards() {
        _cards.value = emptyList()
    }

    fun addCard(userId: Int, context: Context, callback: () -> Unit) {
        val onSuccess = {
            toast(context, "Card added!")
            callback.invoke()
        }
        val onFailure = { message: String ->
            toast(context, message)
        }

        try {
            val authToken = "Bearer " + TokenManager.getInstance(context).getJWTToken()
            repository.addCard(userId, card.value!!.value, authToken, onSuccess, onFailure)
        } catch (e: Exception) {
            toast(context, "Error adding card!")
        }

        updateCard(null)
    }

    fun deleteCard(cardId: Int, context: Context, callback: () -> Unit) {
        val onSuccess = {
            toast(context, "Card deleted!")
            callback.invoke()
        }
        val onFailure = { message: String ->
            toast(context, message)
        }

        try {
            val authToken = "Bearer " + TokenManager.getInstance(context).getJWTToken()
            repository.deleteCard(cardId, authToken, onSuccess, onFailure)
        } catch (e: Exception) {
            toast(context, "Error adding card!")
        }
    }

    private fun toast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
