package hr.foi.air.wattsup.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val title: String, val icon: ImageVector) {
    object Home : Screen("Home", Icons.Default.Home)
    object Images : Screen("Images", Icons.Default.Person)
}

internal val screens = listOf(
    Screen.Home,
    Screen.Images,
)
