package hr.foi.air.wattsup.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.ui.component.CircleButton
import hr.foi.air.wattsup.ui.component.CustomAlertDialog
import hr.foi.air.wattsup.ui.component.GradientImage
import hr.foi.air.wattsup.ui.component.ProgressBarCircle
import hr.foi.air.wattsup.ui.component.ProgressBarFill
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.ui.theme.colorBtnRed
import hr.foi.air.wattsup.ui.theme.colorSilver
import hr.foi.air.wattsup.ui.theme.colorTertiary
import hr.foi.air.wattsup.viewmodels.ChargerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargerPage(onArrowBackClick: () -> Unit, viewModel: ChargerViewModel) {
    var openFullChargeAlertDialog by remember { mutableStateOf(false) }

    // Observe LiveData from the ViewModel
    val charging by viewModel.charging.observeAsState()
    val currentChargeAmount by viewModel.currentChargeAmount.observeAsState()
    val timeElapsed by viewModel.timeElapsed.observeAsState()
    val percentageChargedUntilFull by viewModel.percentageChargedUntilFull.observeAsState()
    val amountNecessaryForFullCharge by viewModel.amountNecessaryForFullCharge.observeAsState()

    Log.i("VIEWMODELChargerPage", "Current Charge Amount: $currentChargeAmount")

    when {
        openFullChargeAlertDialog -> {
            CustomAlertDialog(
                onConfirmation = {
                    openFullChargeAlertDialog = false
                    Log.i("VIEWMODELCOMPOSABLEDISMISS", "$currentChargeAmount")
                },
                dialogTitle = "Charging Status",
                dialogText = "Your vehicle is fully charged. $currentChargeAmount $amountNecessaryForFullCharge ${amountNecessaryForFullCharge!! < 0.01f}",
                icon = Icons.Default.Info,
                showDismissButton = false,
                confirmButtonText = "OK",
            )
        }
    }

    Scaffold(
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
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                GradientImage(
                    R.drawable.icon_electric_car,
                    colorSilver,
                    colorTertiary,
                    currentChargeAmount!!,
                    150,
                    Modifier.fillMaxWidth(),
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Time spent charging:",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Text(
                        text = viewModel.formatTime(timeElapsed!!),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }

                Box {
                    if (charging!!) {
                        ProgressBarCircle(
                            progressBarFill = ProgressBarFill(
                                percentageChargedUntilFull!!,
                                amountNecessaryForFullCharge!!,
                            ),
                            fillColor = MaterialTheme.colorScheme.tertiary,
                            Modifier.size(220.dp).padding(10.dp),
                        )
                    }
                    CircleButton(
                        mode = if (!charging!!) "Start charging" else "Stop charging",
                        onClick = {
                            viewModel.toggleCharging {
                                openFullChargeAlertDialog = true
                                Log.i("VIEWMODELCOMPOSABLE", "$currentChargeAmount")
                            }
                        },
                        color = if (!charging!!) MaterialTheme.colorScheme.primary else colorBtnRed,
                        iconId = null,
                        Modifier.size(220.dp)
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ChargerPagePreview() {
}
