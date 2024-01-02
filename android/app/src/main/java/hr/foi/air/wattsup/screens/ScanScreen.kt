package hr.foi.air.wattsup.screens

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.core.CardManager
import hr.foi.air.wattsup.ui.component.CircleButton
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.viewmodels.ScanViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    title: String,
    addCard: Boolean,
    onArrowBackClick: () -> Unit,
    onScan: () -> Unit,
    viewModel: ScanViewModel,
    cardManagers: List<CardManager>,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val userMessage by viewModel.userMessage.observeAsState()

    val cardStatusMessageList = remember {
        cardManagers.map { cardManager ->
            mutableStateOf(viewModel.getStatusMessage(false, addCard, cardManager))
        }.toMutableList()
    }

    val scanning by viewModel.scanning.observeAsState()
    val scanSuccess by viewModel.scanSuccess.observeAsState()

    LaunchedEffect(snackbarHostState) {
        scope.launch {
            showSnackbarForCardManagers(
                cardManagers,
                cardStatusMessageList,
                snackbarHostState,
                context,
            )
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        snackbarHost = {
            if (!scanning!!) {
                SnackbarHost(hostState = snackbarHostState)
            } else {
                null
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (!scanning!!) {
                        IconButton(onClick = { onArrowBackClick() }) {
                            Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                        }
                    }
                },
            )
        },
    ) {
        val modifier = Modifier.padding(it)

        if (isLandscape) {
            LandscapeChargerLayout(
                scanning,
                scanSuccess,
                cardManagers,
                userMessage,
                { cardManager: CardManager, onScan: () -> Unit ->
                    viewModel.startScanning(
                        cardManager,
                        onScan,
                        addCard,
                    )
                },
                onScan,
                modifier,
            )
        } else {
            PortraitChargerLayout(
                scanning,
                scanSuccess,
                cardManagers,
                userMessage,
                { cardManager: CardManager, onScan: () -> Unit ->
                    viewModel.startScanning(
                        cardManager,
                        onScan,
                        addCard,
                    )
                },
                onScan,
                modifier,
            )
        }
    }
}

private suspend fun showSnackbarForCardManagers(
    cardManagers: List<CardManager>,
    cardStatusMessageList: List<MutableState<String>>,
    snackbarHostState: SnackbarHostState,
    context: Context,
) {
    cardManagers.forEachIndexed { index, cardManager ->
        val cardStatusMessage = cardStatusMessageList[index].value

        val scanResult = snackbarHostState.showSnackbar(
            message = cardStatusMessage,
            actionLabel = if (cardManager.isCardSupportAvailableOnDevice() && !cardManager.isCardSupportEnabledOnDevice()) {
                "Turn on ${cardManager.getName()}"
            } else {
                "OK"
            },
            duration = SnackbarDuration.Indefinite,
        )
        when (scanResult) {
            SnackbarResult.ActionPerformed -> {
                if (cardManager.isCardSupportAvailableOnDevice() && !cardManager.isCardSupportEnabledOnDevice()) {
                    cardManager.showEnableCardSupportOption(context)
                }
            }

            SnackbarResult.Dismissed -> {
            }
        }
    }
}

@Composable
private fun LandscapeChargerLayout(
    scanning: Boolean?,
    scanSuccess: Boolean?,
    cardManagers: List<CardManager>,
    userMessage: String?,
    startScanning: (cardManager: CardManager, onScan: () -> Unit) -> Unit,
    onScan: () -> Unit,
    modifier: Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CardOptions(
                scanning,
                scanSuccess,
                cardManagers,
                userMessage,
                startScanning,
                onScan,
            )
        }
    }
}

@Composable
private fun PortraitChargerLayout(
    scanning: Boolean?,
    scanSuccess: Boolean?,
    cardManagers: List<CardManager>,
    userMessage: String?,
    startScanning: (cardManager: CardManager, onScan: () -> Unit) -> Unit,
    onScan: () -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CardOptions(
                scanning,
                scanSuccess,
                cardManagers,
                userMessage,
                startScanning,
                onScan,
            )
        }
    }
}

@Composable
fun CardOptions(
    scanning: Boolean?,
    scanSuccess: Boolean?,
    cardManagers: List<CardManager>,
    userMessage: String?,
    startScanning: (cardManager: CardManager, onScan: () -> Unit) -> Unit,
    onScan: () -> Unit,
) {
    if (!scanning!! && !scanSuccess!!) {
        cardManagers.forEach { cardManager ->
            CircleButton(
                "Scan ${cardManager.getName()}",
                {
                    startScanning(cardManager, onScan)
                },
                null,
                null,
                Modifier.size(220.dp)
                    .padding(16.dp),
            )
        }
    }
    Text(
        text = if (!scanning) {
            userMessage!!
        } else {
            stringResource(id = R.string.scanning_for_card)
        },
        style = MaterialTheme.typography.titleLarge,
    )
}
