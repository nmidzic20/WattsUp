@file:OptIn(ExperimentalMaterial3Api::class)

package hr.foi.air.wattsup.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import hr.foi.air.wattsup.network.models.Event
import hr.foi.air.wattsup.network.models.TokenManager
import hr.foi.air.wattsup.ui.component.LoadingSpinner
import hr.foi.air.wattsup.ui.theme.colorDarkSilver
import hr.foi.air.wattsup.ui.theme.colorSilver
import hr.foi.air.wattsup.viewmodels.HistoryViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier,
) {
    HistoryView(viewModel, modifier)
}

@Composable
fun HistoryView(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
) {
    val userId = TokenManager.getInstance(context).getUserId()

    val events by viewModel.events.observeAsState(emptyList())
    val showLoading by viewModel.showLoading.observeAsState(true)

    LaunchedEffect(Unit) {
        Log.i("USER_ID", userId.toString())
        // prevent initial API call if events is not empty, which it may not be due use of bottom bar
        // and having still active viewmodel when switching to myCards and back
        if (userId != null && events.isEmpty()) {
            viewModel.refreshHistory(context, userId)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showLoading) {
            item {
                LoadingSpinner(
                    Modifier
                        .height(40.dp)
                        .width(40.dp)
                        .padding(top = 320.dp),
                )
            }
        } else if (events.isEmpty()) {
            item {
                Text(
                    text = "No events to show",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 320.dp),
                )
            }
        } else {
            itemsIndexed(events) { _, item ->
                EventCard(item!!)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(event: Event) {
    val df = DecimalFormat("#.##")
    val showDetails = remember { mutableStateOf(false) }

    if (showDetails.value) {
        DetailDialog(
            event,
            showDetails.value,
            onDismiss = { showDetails.value = !showDetails.value },
        )
    }

    Card(
        onClick = {
            showDetails.value = !showDetails.value
        },
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) colorDarkSilver else colorSilver,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(),
        modifier = Modifier
            .padding(10.dp)
            .height(100.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = SimpleDateFormat("EEEE, dd.MM.yyyy.", Locale.US).format(event.startedAt),
                    fontSize = 20.sp,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = event.chargerLocation,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${df.format(event.volumeKwh)} kWh",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun DetailDialog(
    event: Event,
    showDetails: Boolean,
    onDismiss: () -> Unit,
) {
    val df = DecimalFormat("#.###")

    if (!showDetails) return

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(450.dp)
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Started: ${
                        SimpleDateFormat("dd.MM.yyyy. hh:mm:ss", Locale.US).format(
                            event.startedAt,
                        )
                    }",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = " Ended:  ${
                        SimpleDateFormat("dd.MM.yyyy. hh:mm:ss", Locale.US).format(
                            event.endedAt,
                        )
                    }",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = event.chargerLocation.ifEmpty { "Unnamed Charger" },
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                    )
                    Text(
                        text = "Card: ${event.cardValue}",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text(
                        text = df.format(event.volumeKwh),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                    )
                    Text(
                        text = " kWh",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}
