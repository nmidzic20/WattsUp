package hr.foi.air.wattsup.navigation

sealed class Screens(val route: String) {
    object Home : Screens(HOME_ROUTE)
    object Charger : Screens(CHARGING_ROUTE)
    object History : Screens(USER_ROUTE)
}
