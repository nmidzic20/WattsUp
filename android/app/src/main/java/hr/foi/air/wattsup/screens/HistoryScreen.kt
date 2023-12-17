@file:OptIn(ExperimentalMaterial3Api::class)

package hr.foi.air.wattsup.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.Charger
import hr.foi.air.wattsup.network.models.Event
import hr.foi.air.wattsup.network.models.TokenManager
import hr.foi.air.wattsup.ui.component.TopAppBar
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HistoryScreen(onArrowBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history)) },
                navigationIcon = {
                    IconButton(onClick = { onArrowBackClick() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
            )
        },
    ) {
        HistoryView(it.calculateTopPadding())
    }
}

@Composable
fun HistoryView(topPadding: Dp, context: Context = LocalContext.current) {
    val coroutineScope = rememberCoroutineScope()
    val cards = remember { mutableStateOf(listOf<Card?>()) }
    val events = remember { mutableStateOf(listOf<Event?>()) }
    val showLoading = remember { mutableStateOf(true) }
    val userId = TokenManager.getInstance(context).getId()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            cards.value += getCards(context, userId)
            for (card in cards.value) {
                val data = getEvents(context, card!!.id)
                for (event in data) {
                    event!!.cardValue = card.value
                    event.chargerLocation = getChargerName(context, event.chargerId) ?: ""
                }
                events.value += data
                showLoading.value = false
            }
            events.value = events.value.sortedByDescending { it!!.startedAt }
        }
    }

    Column (
        modifier = Modifier
            .padding(10.dp)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier.padding(top = topPadding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showLoading.value) {
                item {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                            .padding(top = 300.dp)
                    )
                }
            } else if (events.value.isEmpty()) {
                item {
                    Text(
                        text = "No events to show",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .wrapContentHeight(Alignment.CenterVertically)
                    )
                }
            } else {
                itemsIndexed(events.value) { _, item ->
                    EventCard(item!!)
                }
            }
        }
    }
}

@Composable
fun EventCard(event: Event) {
    val showDetails = remember { mutableStateOf(false) }
    val df = DecimalFormat("#.##")

    if (showDetails.value) {
        DetailDialog(event, showDetails)
    }

    Card(
        onClick = {
            showDetails.value = !showDetails.value
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(),
        modifier = Modifier
            .padding(15.dp)
            .height(100.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.chargerLocation,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${df.format(event.volumeKwh)} kWh",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DetailDialog(event: Event, showDetails: MutableState<Boolean>) {
    val df = DecimalFormat("#.###")

    if (!showDetails.value) {
        return
    }

    Dialog(onDismissRequest = { showDetails.value = !showDetails.value }) {
        Column(
            modifier = Modifier
                .width(450.dp)
                .height(200.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Started: ${SimpleDateFormat("dd.MM.yyyy. hh:mm:ss", Locale.US).format(event.startedAt)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " Ended:  ${SimpleDateFormat("dd.MM.yyyy. hh:mm:ss", Locale.US).format(event.endedAt)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = event.chargerLocation.ifEmpty { "Unnamed Charger" },
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "CV: ${event.cardValue}",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = df.format(event.volumeKwh),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = " kWh",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

private suspend fun getEvents(context: Context, cardId: Int) : List<Event?> {
    val eventService = NetworkService.eventService
    val auth = "Bearer " + TokenManager.getInstance(context).getjWTtoken()

    return suspendCoroutine { continuation ->
        eventService.getEvents(cardId, auth).enqueue(object : retrofit2.Callback<List<Event?>> {
            override fun onResponse(call: Call<List<Event?>>, response: Response<List<Event?>>) {
                if (response.isSuccessful) {
                    continuation.resume(response.body() ?: emptyList())
                    Log.d("HistoryScreen", "Events: ${response.body()}")
                } else {
                    Log.d("HistoryScreen", "Error: ${response.errorBody()}")
                    toast(context, "Error: ${response.errorBody()}")
                    continuation.resume(emptyList())
                }
            }

            override fun onFailure(call: Call<List<Event?>>, t: Throwable) {
                Log.d("HistoryScreen", "Failure: ${t.message}")
                toast(context, "Failure: ${t.message}")
                continuation.resume(emptyList())
            }
        })
    }
}

private fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

private suspend fun getCards(context: Context, userId: Int): List<Card?> {
    val cardService = NetworkService.cardService
    val auth = "Bearer " + TokenManager.getInstance(context).getjWTtoken()

    return suspendCoroutine { continuation ->
        cardService.getCardsForUser(userId, auth).enqueue(object : retrofit2.Callback<List<Card?>> {
            override fun onResponse(call: Call<List<Card?>>, response: Response<List<Card?>>) {
                if (response.isSuccessful) {
                    continuation.resume(response.body() ?: emptyList())
                    Log.d("HistoryScreen", "Cards: ${response.body()}")
                } else {
                    Log.d("HistoryScreen", "Error: ${response.errorBody()}")
                    continuation.resume(emptyList())
                }
            }

            override fun onFailure(call: Call<List<Card?>>, t: Throwable) {
                Log.d("HistoryScreen", "Failure: ${t.message}")
                continuation.resume(emptyList())
            }
        })
    }
}

private suspend fun getChargerName(context: Context, chargerId: Int): String? {
    val chargerService = NetworkService.chargerService
    val auth = "Bearer " + TokenManager.getInstance(context).getjWTtoken()

    return suspendCoroutine { continuation ->
        chargerService.getCharger(chargerId, auth).enqueue(object : retrofit2.Callback<Charger?> {
            override fun onResponse(call: Call<Charger?>, response: Response<Charger?>) {
                if (response.isSuccessful) {
                    continuation.resume(response.body()!!.name)
                    Log.d("HistoryScreen", "Cards: ${response.body()}")
                } else {
                    Log.d("HistoryScreen", "Error: ${response.errorBody()}")
                    continuation.resume(null)
                }
            }

            override fun onFailure(call: Call<Charger?>, t: Throwable) {
                Log.d("HistoryScreen", "Failure: ${t.message}")
                continuation.resume(null)
            }
        })
    }
}

@Preview(showBackground = false)
@Composable
fun HistoryPreview() {
    HistoryScreen {}
}