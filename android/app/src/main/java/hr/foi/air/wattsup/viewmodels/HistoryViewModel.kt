package hr.foi.air.wattsup.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.Event
import hr.foi.air.wattsup.repository.WattsUpRepository

class HistoryViewModel(private val repository: WattsUpRepository) : ViewModel() {
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
        _cards.value = repository.getCards(context, userId)
        for (card in _cards.value!!) {
            val data = repository.getEvents(context, card!!.id) { message: String ->
                toast(
                    context,
                    message,
                )
            }
            for (event in data) {
                event!!.cardValue = card.value
                event.chargerLocation = repository.getChargerName(context, event.chargerId) ?: ""
            }
            _events.value = data
        }
        _events.value = _events.value?.sortedByDescending { it!!.startedAt }
        _showLoading.value = false
    }

    private fun toast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
