package hr.foi.air.wattsup.navigation // ktlint-disable filename

import androidx.compose.ui.graphics.vector.ImageVector

data class CustomNavDrawerItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val onClick: () -> Unit,
    val id: Int,
)
