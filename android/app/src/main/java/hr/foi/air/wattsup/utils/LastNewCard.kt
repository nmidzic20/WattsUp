package hr.foi.air.wattsup.utils

import hr.foi.air.wattsup.network.models.Card

interface LastNewCard {
    fun setLastNewCard(newCard: Card)
}
