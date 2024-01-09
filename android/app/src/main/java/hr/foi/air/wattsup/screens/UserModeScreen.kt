@file:OptIn(ExperimentalMaterial3Api::class)

package hr.foi.air.wattsup.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
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
import hr.foi.air.wattsup.ui.component.LogoutDialog
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.viewmodels.CardViewModel
import hr.foi.air.wattsup.viewmodels.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserModeScreen(
    historyViewModel: HistoryViewModel,
    cardsViewModel: CardViewModel,
    onArrowBackClick: () -> Unit,
    onAddCard: () -> Unit,
) {
    val showLogoutDialog = remember { mutableStateOf(false) }
    val selectedRoute = remember { mutableIntStateOf(R.string.cards) }

    val onArrowBack = {
        onArrowBackClick()

        // To ensure API calls for events and cards are performed upon returning to UserModeView
        historyViewModel.resetEvents()
        cardsViewModel.resetCards()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Mode") },
                navigationIcon = {
                    IconButton(onClick = onArrowBack) {
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
                contentColor = Color.White,
                content = {
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults
                            .colors(
                                indicatorColor = MaterialTheme.colorScheme.secondary, // surfaceColorAtElevation(LocalAbsoluteTonalElevation.current)
                            ),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = stringResource(id = R.string.cards),

                            )
                        },
                        label = { Text(text = stringResource(R.string.cards)) },
                        onClick = { selectedRoute.value = R.string.cards },
                        selected = selectedRoute.value == R.string.cards,
                    )
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults
                            .colors(
                                indicatorColor = MaterialTheme.colorScheme.secondary, // surfaceColorAtElevation(LocalAbsoluteTonalElevation.current)
                            ),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = stringResource(id = R.string.history),

                            )
                        },
                        label = { Text(text = stringResource(R.string.history)) },
                        onClick = { selectedRoute.value = R.string.history },
                        selected = selectedRoute.value == R.string.history,
                    )
                },
            )
        },
    ) {
        val modifier = Modifier.padding(it)

        UserModeView(
            selectedRoute,
            onArrowBack,
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
    onArrowBackClick: () -> Unit,
    showLogoutDialog: MutableState<Boolean>,
    historyViewModel: HistoryViewModel,
    cardsViewModel: CardViewModel,
    onAddCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LogoutDialog(
        showLogoutDialog,
        onArrowBackClick,
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
