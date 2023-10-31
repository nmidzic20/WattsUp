package hr.foi.air.wattsup.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.ui.component.CircleButton
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.ui.theme.colorBtnRed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargerPage(onArrowBackClick: () -> Unit) {
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
                var charging by remember { mutableStateOf(false) }
                var startTime by remember { mutableLongStateOf(0L) }
                var endTime by remember { mutableLongStateOf(0L) }
                var timeElapsed by remember { mutableLongStateOf(0L) }
                var timeTrackingJob: Job? by remember { mutableStateOf(null) }

                LaunchedEffect(charging) {
                    // Started charging
                    if (charging) {
                        timeElapsed = 0L
                        startTime = System.currentTimeMillis()
                        timeTrackingJob = CoroutineScope(Dispatchers.Default).launch {
                            while (charging) {
                                // Update time every second
                                delay(1000)
                                timeElapsed = System.currentTimeMillis() - startTime
                            }
                        }
                    }
                    // Stopped charging
                    else {
                        endTime = System.currentTimeMillis()
                        timeTrackingJob?.cancel()
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.icon_electric_car),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Time spent charging:",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Text(
                        text = "${formatTime(timeElapsed)}",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }

                CircleButton(
                    mode = if (!charging) "Start charging" else "Stop charging",
                    onClick = {
                        charging = !charging
                    },
                    color = if (!charging) MaterialTheme.colorScheme.primary else colorBtnRed,
                    iconId = null,
                )
            }
        }
    }
}

fun formatTime(milliseconds: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@Preview
@Composable
fun ChargerPagePreview() {
    ChargerPage {}
}
