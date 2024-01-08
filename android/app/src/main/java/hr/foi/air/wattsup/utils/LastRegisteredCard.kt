package hr.foi.air.wattsup.utils

import androidx.lifecycle.MutableLiveData
import hr.foi.air.wattsup.network.models.Card

object LastRegisteredCard : LastNewCard {
    val userCard: MutableLiveData<Card> by lazy {
        MutableLiveData<Card>()
    }

    override fun setLastNewCard(newCard: Card) {
        userCard.value = newCard
    }
}
