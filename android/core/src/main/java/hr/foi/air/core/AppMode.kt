package hr.foi.air.core

import androidx.compose.runtime.Composable

interface AppMode {

    fun getRoute(): String

    @Composable
    fun displayUI()
}
