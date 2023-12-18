package hr.foi.air.wattsup.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.ui.component.CarChargeIndicator
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.ui.theme.colorDarkGray
import hr.foi.air.wattsup.ui.theme.colorGray
import hr.foi.air.wattsup.ui.theme.colorOrange
import hr.foi.air.wattsup.viewmodels.ChargerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatorScreen(viewModel: ChargerViewModel, onArrowBackClick: () -> Unit) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(it),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CarChargeIndicator(
                modifier = Modifier
                    .size(250.dp)
                    .background(colorDarkGray),
                initialValue = 50,
                primaryColor = colorOrange,
                secondaryColor = colorGray,
                circleRadius = 230f,
                onPositionChange = { position ->
                    Log.i("POSITION", "${position / 100f}")

                    viewModel.updateInitialChargeAmount(position / 100f)
                },
            )

            val options = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5")
            var expanded by remember { mutableStateOf(false) }
            var selectedOptionText by remember { mutableStateOf(options[0]) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
            ) {
                TextField(
                    readOnly = true,
                    value = selectedOptionText,
                    onValueChange = { },
                    label = { Text("Label") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded,
                        )
                    },
                    modifier = Modifier.menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    },
                ) {
                    options.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(text = selectionOption, color = colorDarkGray) },
                            onClick = {
                                selectedOptionText = selectionOption
                                expanded = false
                            },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondary),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Composable
fun SimulatorScreenPreview() {
    SimulatorScreen(ChargerViewModel(), {})
}
