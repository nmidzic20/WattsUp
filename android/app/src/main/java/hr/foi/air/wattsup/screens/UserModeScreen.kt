@file:OptIn(ExperimentalMaterial3Api::class)

package hr.foi.air.wattsup.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.navigation.bottomAppBarItems
import hr.foi.air.wattsup.ui.component.LogoutDialog
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.ui.theme.colorDarkGray
import hr.foi.air.wattsup.ui.theme.colorGray
import hr.foi.air.wattsup.viewmodels.CardViewModel
import hr.foi.air.wattsup.viewmodels.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserModeScreen(
    historyViewModel: HistoryViewModel,
    cardsViewModel: CardViewModel,
    onArrowBackClick: () -> Unit,
    onLogOut: () -> Unit,
    onAddCard: () -> Unit,
    resetUserScreenData: () -> Unit,
) {
    val showLogoutDialog = remember { mutableStateOf(false) }
    val selectedRoute = remember { mutableIntStateOf(R.string.cards) }

    val onLogOutFromUserMode = {
        onLogOut()

        resetUserScreenData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Mode") },
                navigationIcon = {
                    IconButton(onClick = {
                        onArrowBackClick()
                        resetUserScreenData()
                    }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actionIcon = {
                    IconButton(onClick = { showLogoutDialog.value = true }) {
                        Icon(Icons.Filled.ExitToApp, null, tint = Color.White)
                    }
                },
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = if (isSystemInDarkTheme()) Color.White else colorGray,
                contentColor = if (isSystemInDarkTheme()) colorDarkGray else Color.White,
                content = {
                    bottomAppBarItems.forEach { item ->
                        NavigationBarItem(
                            colors = NavigationBarItemDefaults
                                .colors(
                                    indicatorColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = if (isSystemInDarkTheme()) colorDarkGray else Color.White,
                                    unselectedIconColor = if (isSystemInDarkTheme()) colorDarkGray else Color.White,
                                    unselectedTextColor = if (isSystemInDarkTheme()) colorDarkGray else Color.White,
                                ),
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(id = item.labelResId),

                                )
                            },
                            label = { Text(text = stringResource(item.labelResId)) },
                            onClick = { selectedRoute.value = item.labelResId },
                            selected = selectedRoute.value == item.labelResId,
                        )
                    }
                },
            )
        },
    ) {
        val modifier = Modifier.padding(it)

        UserModeView(
            selectedRoute,
            onLogOutFromUserMode,
            showLogoutDialog,
            historyViewModel,
            cardsViewModel,
            onAddCard,
            modifier,
        )
    }
}

@Composable
fun UserModeView(
    selectedRoute: MutableIntState,
    onLogOut: () -> Unit,
    showLogoutDialog: MutableState<Boolean>,
    historyViewModel: HistoryViewModel,
    cardsViewModel: CardViewModel,
    onAddCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LogoutDialog(
        showLogoutDialog,
        onLogOut,
    )

    when (selectedRoute.value) {
        R.string.history ->
            HistoryScreen(
                viewModel = historyViewModel,
                modifier = modifier,
            )

        R.string.cards -> CardScreen(
            onAddCard = onAddCard,
            viewModel = cardsViewModel,
            modifier = modifier,
        )
    }

    /*Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ElevatedButton(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            onClick = onHistoryClick,
        ) {
            Text(
                text = stringResource(R.string.history),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.width(150.dp),
                textAlign = TextAlign.Center,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        ElevatedButton(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            onClick = onCardsClick,
        ) {
            Text(
                text = stringResource(R.string.cards),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.width(150.dp),
                textAlign = TextAlign.Center,
            )
        }
    }*/
}
