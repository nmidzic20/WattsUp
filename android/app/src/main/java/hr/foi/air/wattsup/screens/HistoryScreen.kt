@file:OptIn(ExperimentalMaterial3Api::class)

package hr.foi.air.wattsup.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.Event
import hr.foi.air.wattsup.network.models.EventGETBody
import hr.foi.air.wattsup.network.models.EventGETResponseBody
import hr.foi.air.wattsup.network.models.EventPUTResponseBody
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.utils.UserCard
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

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
    val data = listOf(
        Event(
            cardId = 1,
            chargerId = 1,
            startedAt = Date(),
            endedAt = Date(),
            volumeKwh = 10.0f
        ),
        Event(
            cardId = 1,
            chargerId = 1,
            startedAt = Date(),
            endedAt = Date(),
            volumeKwh = 10.0f
        ),
        Event(
            cardId = 1,
            chargerId = 1,
            startedAt = Date(),
            endedAt = Date(),
            volumeKwh = 10.0f
        ),
        Event(
            cardId = 1,
            chargerId = 1,
            startedAt = Date(),
            endedAt = Date(),
            volumeKwh = 10.0f
        ),
        Event(
            cardId = 1,
            chargerId = 1,
            startedAt = Date(),
            endedAt = Date(),
            volumeKwh = 10.0f
        ),
        Event(
            cardId = 1,
            chargerId = 1,
            startedAt = Date(),
            endedAt = Date(),
            volumeKwh = 10.0f
        ),
        Event(
            cardId = 1,
            chargerId = 1,
            startedAt = Date(),
            endedAt = Date(),
            volumeKwh = 10.0f
        ),
        Event(
            cardId = 1,
            chargerId = 1,
            startedAt = Date(),
            endedAt = Date(),
            volumeKwh = 10.0f
        ),
        Event(
            cardId = 1,
            chargerId = 1,
            startedAt = Date(),
            endedAt = Date(),
            volumeKwh = 10.0f
        ),
        Event(
            cardId = 1,
            chargerId = 1,
            startedAt = Date(),
            endedAt = Date(),
            volumeKwh = 10.0f
        ),
    )
    getEventItems(1)
    Column (
        modifier = Modifier
            .padding(10.dp)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(top = topPadding),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(data) { index, item ->
                EventCard(index)
            }
        }
    }
}

private fun getEventItems(cardId: Long) {
    val _eventService = NetworkService.eventService

    _eventService.getEvents(cardId).enqueue(object : retrofit2.Callback<List<Event?>> {
        override fun onResponse(call: Call<List<Event?>>, response: Response<List<Event?>>) {
            if (response.isSuccessful) {
                val events: List<Event?>? = response.body()
                if (events != null) {
                    Log.d("HistoryScreen", "Response: ${response.body()}")
                }
            } else {
                Log.d("HistoryScreen", "Response: ${response.errorBody()}")
            }
        }

        override fun onFailure(call: Call<List<Event?>>, t: Throwable) {
            Log.d("HistoryScreen", "Response: ${t.message}")
        }
    })
}

@Composable
fun EventCard(index: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(10.dp)
            .height(100.dp)
            .fillMaxWidth()
    ) {
        Column {
            Text(text = "example")
        }
    }
}

@Preview(showBackground = false)
@Composable
fun HistoryPreview() {
    HistoryScreen {}
}