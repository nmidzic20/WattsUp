package hr.foi.air.wattsup.network.models

import java.util.Date

data class Event(
    val cardId: Int,
    val chargerId: Int,
    val startedAt: Date,
    val endedAt: Date,
    val volumeKwh: Float,
)