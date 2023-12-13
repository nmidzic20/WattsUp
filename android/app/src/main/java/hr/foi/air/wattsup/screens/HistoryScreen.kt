@file:OptIn(ExperimentalMaterial3Api::class)

package hr.foi.air.wattsup.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.Event
import hr.foi.air.wattsup.network.models.EventGETBody
import hr.foi.air.wattsup.network.models.EventGETResponseBody
import hr.foi.air.wattsup.network.models.EventPUTResponseBody
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.utils.UserCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
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
fun HistoryView(topPadding: Dp) {
    val coroutineScope = rememberCoroutineScope()
    val data = remember { mutableStateOf(listOf<Event?>()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            data.value = getEventItems(/*UserCard.userCard.value?.id!!.toLong()*/1)
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
            if (data.value.isEmpty()) {
                item {
                    Text(
                        text = "No events to show",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
            } else {
                itemsIndexed(data.value) { index, item ->
                    EventCard(index, item!!)
                }
            }
        }
    }
}

@Composable
fun EventCard(index: Int, event: Event) {
    val showDetails = remember { mutableStateOf(false) }

    if (showDetails.value) {
        DetailBox(event)
    }

    Card(
        onClick = {
            showDetails.value = true
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
                    // display full day of week
                    text = "${SimpleDateFormat("EEEE").format(event.startedAt)}, " +
                           "${SimpleDateFormat("dd.MM.yyyy.").format(event.startedAt)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
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
                    text = "Station ${event.chargerId}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${event.volumeKwh} kWh",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DetailBox(event: Event) {
    val show = remember { mutableStateOf(true) }

    if (!show.value) {
        return
    }

    Dialog(onDismissRequest = { show.value = false }) {
        Column(
            modifier = Modifier
                .height(300.dp)
                .width(300.dp)
                .padding(10.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Started at ${SimpleDateFormat("hh:mm:ss dd.mm.yyyy.").format(event.startedAt)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Ended at ${SimpleDateFormat("hh:mm:ss dd.mm.yyyy.").format(event.endedAt)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Card ${event.cardId}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Location ${event.chargerId}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${event.volumeKwh} kWh",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private suspend fun getEventItems(cardId: Long) : List<Event?> {
    val _eventService = NetworkService.eventService

    return suspendCoroutine { continuation ->
        _eventService.getEvents(cardId).enqueue(object : retrofit2.Callback<List<Event?>> {
            override fun onResponse(call: Call<List<Event?>>, response: Response<List<Event?>>) {
                if (response.isSuccessful) {
                    continuation.resume(response.body() ?: emptyList())
                    Log.d("HistoryScreen", "Events: ${response.body()}")
                } else {
                    Log.d("HistoryScreen", "Error: ${response.errorBody()}")
                    continuation.resume(emptyList())
                }
            }

            override fun onFailure(call: Call<List<Event?>>, t: Throwable) {
                Log.d("HistoryScreen", "Failure: ${t.message}")
                continuation.resume(emptyList())
            }
        })
    }
}

@Preview(showBackground = false)
@Composable
fun HistoryPreview() {
    HistoryScreen {}
}