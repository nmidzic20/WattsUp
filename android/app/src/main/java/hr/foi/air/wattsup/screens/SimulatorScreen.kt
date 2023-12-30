package hr.foi.air.wattsup.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.ui.component.CarChargeIndicator
import hr.foi.air.wattsup.ui.component.DropdownMenu
import hr.foi.air.wattsup.ui.component.GradientImage
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.ui.theme.colorDarkGray
import hr.foi.air.wattsup.ui.theme.colorGray
import hr.foi.air.wattsup.ui.theme.colorOrange
import hr.foi.air.wattsup.ui.theme.colorSilver
import hr.foi.air.wattsup.viewmodels.ChargerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatorScreen(viewModel: ChargerViewModel, onArrowBackClick: () -> Unit) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EV Simulator") },
                navigationIcon = {
                    IconButton(onClick = { onArrowBackClick() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                modifier = Modifier
                    .height(40.dp),
            )
        },
        containerColor = colorDarkGray,
    ) {
        val modifier = Modifier.padding(it)

        if (isLandscape) {
            LandscapeChargerLayout(viewModel, modifier)
        } else {
            PortraitChargerLayout(viewModel, modifier)
        }
    }
}

@Composable
fun PortraitChargerLayout(viewModel: ChargerViewModel, modifier: Modifier = Modifier) {
    val currentChargeAmount by viewModel.currentChargeAmount.observeAsState()
    val height = 150
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        GradientImage(
            R.drawable.icon_electric_car,
            colorSilver,
            MaterialTheme.colorScheme.secondary,
            currentChargeAmount!!,
            height,
            Modifier.size(height.dp),
        )

        SimulatorView(viewModel)
    }
}

@Composable
fun LandscapeChargerLayout(viewModel: ChargerViewModel, modifier: Modifier = Modifier) {
    val currentChargeAmount by viewModel.currentChargeAmount.observeAsState()
    val height = 150
    Row(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GradientImage(
            R.drawable.icon_electric_car,
            colorSilver,
            MaterialTheme.colorScheme.secondary,
            currentChargeAmount!!,
            height,
            Modifier.size(height.dp),
        )
        SimulatorView(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatorView(viewModel: ChargerViewModel) {
    val lastSelectedInitialValue by viewModel.lastSelectedInitialChargeValue.observeAsState()

    CarChargeIndicator(
        modifier = Modifier
            .size(250.dp)
            .background(colorDarkGray),
        initialValue = lastSelectedInitialValue!!,
        primaryColor = colorOrange,
        secondaryColor = colorGray,
        circleRadius = 230f,
        onPositionChange = { position ->
            viewModel.updateInitialChargeAmount(position / 100f)
        },
        onDrag = { position ->
            viewModel.updateInitialChargeAmount(position / 100f)
        },
    )

    LaunchedEffect(key1 = Unit) {
        viewModel.getChargers()
    }
    val chargers by viewModel.chargerList.observeAsState(emptyList())
    val options = chargers.map { charger -> charger!!.name }
    var expanded by remember { mutableStateOf(false) }
    val lastSelectedCharger by viewModel.lastSelectedCharger.observeAsState()
    var selectedOptionText by remember { mutableStateOf<String>(lastSelectedCharger?.name ?: "") }

    DropdownMenu(
        labelText = "Choose charger",
        options = options,
        selectedOptionText = selectedOptionText,
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        onDismissRequest = {
            expanded = false
        },
        onClickMenuItem = { selectionOption ->
            selectedOptionText = selectionOption
            expanded = false
            val selectedCharger = chargers.find { charger -> charger!!.name == selectionOption }
            viewModel.updateSelectedCharger(selectedCharger!!)
        },
    )
}

@Preview
@Composable
fun SimulatorScreenPreview() {
    SimulatorScreen(ChargerViewModel(), {})
}
