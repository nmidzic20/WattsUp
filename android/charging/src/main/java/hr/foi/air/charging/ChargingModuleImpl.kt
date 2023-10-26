package hr.foi.air.charging

import androidx.compose.runtime.Composable
import hr.foi.air.core.AppMode

class ChargingModuleImpl : AppMode {

    override fun getRoute(): String {
        return "Charger"
    }

    @Composable
    override fun displayUI() {
        ChargerScreen()
    }
}
