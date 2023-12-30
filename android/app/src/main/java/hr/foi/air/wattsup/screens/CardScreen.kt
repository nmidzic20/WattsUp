@file:OptIn(ExperimentalMaterial3Api::class)

package hr.foi.air.wattsup.screens

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import hr.foi.air.wattsup.R
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.TokenManager
import hr.foi.air.wattsup.ui.component.CircleButton
import hr.foi.air.wattsup.ui.component.TopAppBar
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CardScreen(onArrowBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cards)) },
                navigationIcon = {
                    IconButton(onClick = { onArrowBackClick() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
            )
        },
    ) {
        CardView()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardView(context: Context = LocalContext.current) {
    val coroutineScope = rememberCoroutineScope()
    val cards = remember { mutableStateOf(listOf<Card?>()) }
    val showLoading = remember { mutableStateOf(true) }
    val userId = TokenManager.getInstance(context).getId()
    val state = rememberLazyListState()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            cards.value += getCards(context, userId)
            showLoading.value = false
        }
    }

    Column (
        modifier = Modifier
            .padding(top = 30.dp)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            verticalAlignment = Alignment.CenterVertically,
            state = state,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = state)
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
            } else if (cards.value.isEmpty()) {
                item {
                    Text(
                        text = "You have no cards",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .wrapContentHeight(Alignment.CenterVertically)
                    )
                }
            } else {
                itemsIndexed(cards.value) { _, item ->
                    CardCard(item!!)
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ElevatedButton(
                onClick = { /*TODO*/ },
            ) {
                Text(
                    text = "Add Card",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.width(120.dp),
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}

@Composable
fun CardCard(item: Card) {
    val width = LocalConfiguration.current.screenWidthDp.dp * 0.92f
    val showRemoveDialog = remember { mutableStateOf(false) }

    RemoveDialog(showRemoveDialog)

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp)
            .height(200.dp)
            .width(width)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircleButton(
                    onClick = { },
                    modifier = Modifier
                        .height(7.dp)
                        .width(7.dp)
                        .align(Alignment.Start),
                    iconId = null,
                    color = if (item.active) Color.Green else Color.Red,
                    mode = "fill",
                )
                Text(
                    text = item.value,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp)
                )
                ElevatedButton(
                    onClick = { showRemoveDialog.value = true },
                    colors = ButtonDefaults.elevatedButtonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.End)
                ) {
                    Text(
                        text = "Remove",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun RemoveDialog(openAlertDialog: MutableState<Boolean>) {
    when {
        openAlertDialog.value -> {
            Dialog(onDismissRequest = { openAlertDialog.value = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            fontSize = 20.sp,
                            text = "Remove this card?",
                            modifier = Modifier.padding(16.dp),
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            TextButton(
                                onClick = { openAlertDialog.value = false },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontSize = 16.sp,
                                    color = Color.LightGray
                                )
                            }
                            TextButton(
                                onClick = { openAlertDialog.value = false; },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text(
                                    text = "Yes",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
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
                    Log.d("CardScreen", "Cards: ${response.body()}")
                } else {
                    Log.d("CardScreen", "Error: ${response.errorBody()}")
                    continuation.resume(emptyList())
                }
            }

            override fun onFailure(call: Call<List<Card?>>, t: Throwable) {
                Log.d("CardScreen", "Failure: ${t.message}")
                continuation.resume(emptyList())
            }
        })
    }
}

@Preview(showBackground = false)
@Composable
fun CardPreview() {
    CardScreen {}
}