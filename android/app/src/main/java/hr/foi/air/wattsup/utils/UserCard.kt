package hr.foi.air.wattsup.utils

import androidx.lifecycle.MutableLiveData
import hr.foi.air.wattsup.network.models.Card

object UserCard {
    val userCard: MutableLiveData<Card> by lazy{
        MutableLiveData<Card>()
    }
}