package hr.foi.air.wattsup.network.models

import java.util.Date

data class Charger(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: Date,
    val createdBy: Int,
    val lastSyncAt: Date,
    val active: Boolean
)