package hr.foi.air.wattsup.navigation

import hr.foi.air.wattsup.R

const val HOME_ROUTE = "Home"
const val CHARGING_ROUTE = "Charger"
const val USER_ROUTE = "History"

data class NavigationItem(
    val route: String = "",
    val selectedIconId: Int = 0,
    val unselectedIconId: Int = 0,
    val labelId: Int = 0,
) {
    fun navigationItems(): List<NavigationItem> {
        return listOf(
            NavigationItem(
                route = HOME_ROUTE,
                selectedIconId = R.drawable.ic_home_filled,
                unselectedIconId = R.drawable.ic_home_outlined,
                labelId = R.string.home,
            ),
            NavigationItem(
                route = CHARGING_ROUTE,
                selectedIconId = R.drawable.ic_home_filled,
                unselectedIconId = R.drawable.ic_home_outlined,
                labelId = R.string.charger,
            ),
            NavigationItem(
                route = USER_ROUTE,
                selectedIconId = R.drawable.ic_home_filled,
                unselectedIconId = R.drawable.ic_home_outlined,
                labelId = R.string.history,
            ),
        )
    }
}
