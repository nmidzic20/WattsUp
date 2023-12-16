package hr.foi.air.wattsup.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import hr.foi.air.wattsup.viewmodels.ChargerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargerScreen(onArrowBackClick: () -> Unit, viewModel: ChargerViewModel) {
    val openFullChargeAlertDialog by viewModel.openFullChargeAlertDialog.observeAsState()
    val charging by viewModel.charging.observeAsState()
    val currentChargeAmount by viewModel.currentChargeAmount.observeAsState()
    val timeElapsed by viewModel.timeElapsed.observeAsState()
    val percentageChargedUntilFull by viewModel.percentageChargedUntilFull.observeAsState()
    val amountNecessaryForFullCharge by viewModel.amountNecessaryForFullCharge.observeAsState()
    val toastMessage by viewModel.toastMessage.observeAsState()

    if (toastMessage != null) {
        Toast.makeText(LocalContext.current, toastMessage, Toast.LENGTH_SHORT).show()
        viewModel.clearToastMessage()
    }

    when {
        openFullChargeAlertDialog == true -> {
            CustomAlertDialog(
                onConfirmation = {
                    viewModel.setOpenFullChargeAlertDialog(false)
                },
                dialogTitle = "Charging Status",
                dialogText = "Your vehicle is fully charged.",
                icon = Icons.Default.Info,
                showDismissButton = false,
                confirmButtonText = "OK",
            )
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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
        val modifier = Modifier.padding(it)

        if (isLandscape) {
            LandscapeChargerLayout(
                viewModel,
                currentChargeAmount!!,
                charging!!,
                timeElapsed!!,
                percentageChargedUntilFull!!,
                amountNecessaryForFullCharge!!,
                modifier,
            )
        } else {
            PortraitChargerLayout(
                viewModel,
                currentChargeAmount!!,
                charging!!,
                timeElapsed!!,
                percentageChargedUntilFull!!,
                amountNecessaryForFullCharge!!,
                modifier,
            )
        }
    }
}

@Composable
private fun LandscapeChargerLayout(
    viewModel: ChargerViewModel,
    currentChargeAmount: Float,
    charging: Boolean,
    timeElapsed: Long,
    percentageChargedUntilFull: Float,
    amountNecessaryForFullCharge: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChargingIndicators(
            viewModel,
            currentChargeAmount,
            charging,
            timeElapsed,
            percentageChargedUntilFull,
            amountNecessaryForFullCharge,
        )
    }
}

@Composable
private fun PortraitChargerLayout(
    viewModel: ChargerViewModel,
    currentChargeAmount: Float,
    charging: Boolean,
    timeElapsed: Long,
    percentageChargedUntilFull: Float,
    amountNecessaryForFullCharge: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        ChargingIndicators(
            viewModel,
            currentChargeAmount,
            charging,
            timeElapsed,
            percentageChargedUntilFull,
            amountNecessaryForFullCharge,
        )
    }
}

@Composable
fun ChargingIndicators(
    viewModel: ChargerViewModel,
    currentChargeAmount: Float,
    charging: Boolean,
    timeElapsed: Long,
    percentageChargedUntilFull: Float,
    amountNecessaryForFullCharge: Float,
) {
    val height = 150
    GradientImage(
        R.drawable.icon_electric_car,
        colorSilver,
        MaterialTheme.colorScheme.secondary,
        currentChargeAmount,
        height,
        Modifier.size(height.dp),
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Time spent charging:",
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = viewModel.formatTime(timeElapsed),
            style = MaterialTheme.typography.titleLarge,
        )
    }

    Box {
        if (charging) {
            ProgressBarCircle(
                progressBarFill = ProgressBarFill(
                    percentageChargedUntilFull,
                    amountNecessaryForFullCharge,
                ),
                fillColor = MaterialTheme.colorScheme.secondary,
                Modifier.size(220.dp).padding(10.dp),
            )
        }
        CircleButton(
            mode = if (!charging) "Start charging" else "Stop charging",
            onClick = {
                viewModel.toggleCharging {
                    viewModel.setOpenFullChargeAlertDialog(true)
                }
            },
            color = if (!charging) MaterialTheme.colorScheme.primary else colorBtnRed,
            iconId = null,
            Modifier.size(220.dp)
                .padding(16.dp),
        )
    }
}

@Preview(
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape",
)
@Composable
fun ChargerScreenPreview() {
    ChargerScreen({}, ChargerViewModel())
}
