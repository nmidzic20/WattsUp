@file:OptIn(ExperimentalMaterial3Api::class)

package hr.foi.air.wattsup.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.EventGETBody
import hr.foi.air.wattsup.network.models.EventGETResponseBody
import hr.foi.air.wattsup.network.models.EventPUTResponseBody
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.utils.UserCard
import retrofit2.Call
import retrofit2.Response

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
        HistoryView()
    }
}

@Composable
fun HistoryView() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(0.dp, 15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // make mock item
        item {
            Text(
                text = "Item 1",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp)
            )
        }

    }
}

private fun getEventItems(eventGETBody: EventGETBody) {
    val _eventService = NetworkService.eventService

    _eventService.getEvents(eventGETBody).enqueue(object : retrofit2.Callback<EventGETResponseBody> {
        override fun onResponse(
            call: Call<EventGETResponseBody>,
            response: Response<EventGETResponseBody>,
        ) {
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Log.d("HistoryScreen", "Response: ${responseBody.events}")
                }
            } else {
                Log.d("HistoryScreen", "Response: ${response.errorBody()}")
            }
        }

        override fun onFailure(call: Call<EventGETResponseBody>, t: Throwable) {
            Log.d("HistoryScreen", "Response: ${t.message}")
        }
    })
}

@Preview(showBackground = false)
@Composable
fun HistoryPreview() {
    HistoryScreen {}
}