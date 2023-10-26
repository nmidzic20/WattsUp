package hr.foi.air.user_mode // ktlint-disable package-name

import androidx.compose.runtime.Composable
import hr.foi.air.core.AppMode

class UserModuleImpl : AppMode {

    override fun getRoute(): String {
        return "History"
    }

    @Composable
    override fun displayUI() {
        HistoryScreen()
    }
}
