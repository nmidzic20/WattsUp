@file:OptIn(ExperimentalMaterial3Api::class)

package hr.foi.air.wattsup.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.network.models.TokenManager
import hr.foi.air.wattsup.ui.component.CircleButton
import hr.foi.air.wattsup.ui.component.TopAppBar
import hr.foi.air.wattsup.viewmodels.CardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CardScreen(onArrowBackClick: () -> Unit, onAddCard: () -> Unit, viewModel: CardViewModel) {
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
        CardView(viewModel, onAddCard)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardView(viewModel: CardViewModel, onAddCard: () -> Unit, context: Context = LocalContext.current) {
    val userId = TokenManager.getInstance(context).getId()
    val state = rememberLazyListState()
    val cards by viewModel.cards.observeAsState(emptyList())
    val showLoading by viewModel.showLoading.observeAsState(true)
    val scannedCard by viewModel.card.observeAsState()
    val refresh = remember { mutableStateOf(false) }
    val currentCard = remember { derivedStateOf { state.firstVisibleItemIndex } }

    AddDialog(refresh, scannedCard, userId, viewModel)

    LaunchedEffect(Unit, refresh.value) {
        viewModel.refreshCards(context, userId)
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement
            .spacedBy(space = 20.dp, alignment = Alignment.CenterVertically),
    ) {
        PageIndicator(
            numberOfPages = cards.size,
            selectedPage = currentCard.value,
            defaultRadius = 8.dp,
            selectedLength = 20.dp,
            space = 5.dp,
            animationDurationInMillis = 500,
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            state = state,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = state)
        ) {
            if (showLoading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentHeight(Alignment.CenterVertically)
                    )
                }
            } else if (cards.isEmpty()) {
                item {
                    Text(
                        text = "You have no cards",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentHeight(Alignment.CenterVertically)
                    )
                }
            } else {
                itemsIndexed(cards) { _, item ->
                    CardCard(refresh, item!!, viewModel)
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        ElevatedButton(
            onClick = { onAddCard() },
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

@Composable
fun CardCard(refresh: MutableState<Boolean>, item: Card, viewModel: CardViewModel) {
    val width = LocalConfiguration.current.screenWidthDp.dp * 0.92f
    val showRemoveDialog = remember { mutableStateOf(false) }
    val selectedCardId = remember { mutableIntStateOf(-1) }

    RemoveDialog(showRemoveDialog, refresh, selectedCardId, viewModel)

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
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
                    onClick = { showRemoveDialog.value = true; selectedCardId.intValue = item.id },
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
private fun RemoveDialog(
    openAlertDialog: MutableState<Boolean>,
    refresh: MutableState<Boolean>,
    cardId: MutableState<Int>,
    viewModel: CardViewModel,
    context : Context = LocalContext.current) {
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
                            color = Color.White
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
                                onClick = {
                                    openAlertDialog.value = false
                                    viewModel.deleteCard(cardId.value, context)
                                    refresh.value = !refresh.value
                                },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text(
                                    text = "Remove",
                                    fontSize = 16.sp,
                                    color = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddDialog(
    refresh: MutableState<Boolean>,
    scannedCard: Card?,
    userId: Int,
    viewModel: CardViewModel,
    context: Context = LocalContext.current) {
    when {
        scannedCard != null -> {
            Dialog(onDismissRequest = { viewModel.updateCard(null) }) {
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
                            text = "Add card ${scannedCard.value}?",
                            modifier = Modifier.padding(16.dp),
                            color = Color.White
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            TextButton(
                                onClick = { viewModel.updateCard(null) },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontSize = 16.sp,
                                    color = Color.LightGray
                                )
                            }
                            TextButton(
                                onClick = {
                                    viewModel.addCard(userId, context)
                                    refresh.value = !refresh.value
                                },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text(
                                    text = "Add",
                                    fontSize = 16.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PageIndicator(
    numberOfPages: Int,
    modifier: Modifier = Modifier,
    selectedPage: Int = 0,
    selectedColor: Color = Color.Green,
    defaultColor: Color = Color.LightGray,
    defaultRadius: Dp = 20.dp,
    selectedLength: Dp = 60.dp,
    space: Dp = 30.dp,
    animationDurationInMillis: Int = 300,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space),
        modifier = modifier,
    ) {
        for (i in 0 until numberOfPages) {
            val isSelected = i == selectedPage
            PageIndicatorView(
                isSelected = isSelected,
                selectedColor = selectedColor,
                defaultColor = defaultColor,
                defaultRadius = defaultRadius,
                selectedLength = selectedLength,
                animationDurationInMillis = animationDurationInMillis,
            )
        }
    }
}

@Composable
fun PageIndicatorView(
    isSelected: Boolean,
    selectedColor: Color,
    defaultColor: Color,
    defaultRadius: Dp,
    selectedLength: Dp,
    animationDurationInMillis: Int,
    modifier: Modifier = Modifier,
) {

    val color: Color by animateColorAsState(
        targetValue = if (isSelected) {
            selectedColor
        } else {
            defaultColor
        },
        animationSpec = tween(
            durationMillis = animationDurationInMillis,
        ), label = ""
    )
    val width: Dp by animateDpAsState(
        targetValue = if (isSelected) {
            selectedLength
        } else {
            defaultRadius
        },
        animationSpec = tween(
            durationMillis = animationDurationInMillis,
        ), label = ""
    )

    Canvas(
        modifier = modifier
            .size(
                width = width,
                height = defaultRadius,
            ),
    ) {
        drawRoundRect(
            color = color,
            topLeft = Offset.Zero,
            size = Size(
                width = width.toPx(),
                height = defaultRadius.toPx(),
            ),
            cornerRadius = CornerRadius(
                x = defaultRadius.toPx(),
                y = defaultRadius.toPx(),
            ),
        )
    }
}

@Preview(showBackground = false)
@Composable
fun CardPreview() {
    CardScreen({}, {}, CardViewModel())
}