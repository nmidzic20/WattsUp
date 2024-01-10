package hr.foi.air.wattsup.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.ui.graphics.vector.ImageVector
import hr.foi.air.wattsup.R

data class BottomAppBarItem(
    val icon: ImageVector,
    val labelResId: Int,
)

val bottomAppBarItems = listOf(
    BottomAppBarItem(
        icon = Icons.Default.CreditCard,
        labelResId = R.string.cards,
    ),
    BottomAppBarItem(
        icon = Icons.Default.History,
        labelResId = R.string.history,
    ),
)
