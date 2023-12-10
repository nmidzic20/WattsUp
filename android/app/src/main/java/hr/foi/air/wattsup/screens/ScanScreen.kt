package hr.foi.air.wattsup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            mutableStateOf(viewModel.getStatusMessage(false, cardManager))
        }.toMutableList()
    }

    val scanning by viewModel.scanning.observeAsState()
    val scanSuccess by viewModel.scanSuccess.observeAsState()

    LaunchedEffect(snackbarHostState) {
        scope.launch {
            cardManagers.forEachIndexed { index, cardManager ->
                val cardStatusMessage = cardStatusMessageList[index].value

                val scanResult =
                    snackbarHostState
                        .showSnackbar(
                            message = cardStatusMessage,
                            actionLabel = if (cardManager
                                    .isCardSupportAvailableOnDevice() && !cardManager
                                    .isCardSupportEnabledOnDevice()
                            ) {
                                "Turn on ${cardManager.getName()}"
                            } else {
                                "OK"
                            },
                            duration = SnackbarDuration.Indefinite,
                        )
                when (scanResult) {
                    SnackbarResult.ActionPerformed -> {
                        if (cardManager.isCardSupportAvailableOnDevice() &&
                            !cardManager.isCardSupportEnabledOnDevice()
                        ) {
                            cardManager.showEnableCardSupportOption(context)
                        }
                    }

                    SnackbarResult.Dismissed -> {
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.charger_mode)) },
                navigationIcon = {
                    IconButton(onClick = { onArrowBackClick() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(it),
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
                if (!scanning!! && !scanSuccess!!) {
                    cardManagers.forEach { cardManager ->
                        CircleButton(
                            "Scan ${cardManager.getName()}",
                            {
                                viewModel.startScanning(cardManager, onScan)
                            },
                            null,
                            null,
                            Modifier.size(220.dp)
                                .padding(16.dp),
                        )
                    }
                }
                Text(
                    text = if (!scanning!!) {
                        userMessage!!
                    } else {
                        "Scanning for card..."
                    },
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}
