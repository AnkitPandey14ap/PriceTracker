package com.multibank.pricetracker.ui.feature.feed

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.multibank.pricetracker.ui.common.ToastHelper
import com.multibank.pricetracker.ui.feature.feed.mvi.FeedIntent
import com.multibank.pricetracker.ui.feature.feed.mvi.FeedSideEffect
import com.multibank.pricetracker.ui.feature.feed.bean.ConnectionStateUi
import kotlin.reflect.KClass
import com.multibank.pricetracker.ui.feature.feed.bean.FeedItemUi
import com.multibank.pricetracker.ui.feature.feed.bean.PriceDirectionUi
import com.multibank.pricetracker.ui.util.formatPrice
import com.multibank.pricetracker.ui.util.formatPriceChange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onSymbolClick: (String) -> Unit,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.feedSideEffect.collect {
            when (it) {
                is FeedSideEffect.NavigateToDetailPage -> onSymbolClick(it.id)
                is FeedSideEffect.ShowToast -> ToastHelper.show(context, it.text)
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ConnectionDot(state = uiState.connectionState)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Price Tracker",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    ToggleFeedButton(
                        isRunning = uiState.isFeedRunning,
                        onClick = { viewModel.sendIntent(FeedIntent.ToggleConnection) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }
            itemsIndexed(
                items = uiState.stocks,
                key = { _, stock -> stock.symbol }
            ) { index, stock ->
                val flashState = uiState.flashMap[stock.symbol]
                StockRow(
                    stock = stock,
                    rank = index + 1,
                    flashState = flashState,
                    onClick = { onSymbolClick(stock.symbol) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

    }
}


@Composable
private fun ToggleFeedButton(isRunning: Boolean, onClick: () -> Unit) {
    val containerColor = if (isRunning) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
    val contentColor = if (isRunning) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = if (isRunning) "⏹ Stop" else "▶ Start",
            fontWeight = FontWeight.SemiBold
        )
    }
}


@Composable
private fun ConnectionDot(state: ConnectionStateUi) {
    val color = when (state) {
        is ConnectionStateUi.Connected -> Color(0xFF4CAF50)
        is ConnectionStateUi.Disconnected -> Color(0xFFF44336)
        is ConnectionStateUi.Connecting -> Color(0xFFFF9800)
    }
    Box(
        modifier = Modifier
            .size(11.dp)
            .clip(CircleShape)
            .background(color)
    )
}


@Composable
private fun StockRow(
    stock: FeedItemUi,
    rank: Int,
    flashState: Boolean?,
    onClick: () -> Unit
) {
    val defaultCardColor = MaterialTheme.colorScheme.surface
    val flashGreen = Color(0xFF4CAF50).copy(alpha = 0.12f)
    val flashRed = Color(0xFFF44336).copy(alpha = 0.12f)

    val targetColor = when (flashState) {
        true -> flashGreen
        false -> flashRed
        null -> defaultCardColor
    }

    val animatedCardColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 600, easing = EaseInOut),
        label = "cardFlash_${stock.symbol}"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(CornerSize(4)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = animatedCardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(32.dp)
            )

            // Symbol + direction indicator
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stock.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val arrow = when (stock.direction) {
                        PriceDirectionUi.UP -> "↑"
                        PriceDirectionUi.DOWN -> "↓"
                        PriceDirectionUi.NEUTRAL -> "—"
                    }
                    val arrowColor = when (stock.direction) {
                        PriceDirectionUi.UP -> Color(0xFF4CAF50)
                        PriceDirectionUi.DOWN -> Color(0xFFF44336)
                        PriceDirectionUi.NEUTRAL -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(
                        text = arrow,
                        color = arrowColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Text(
                    text = formatPriceChange(stock.priceChange, stock.priceChangePercent),
                    style = MaterialTheme.typography.labelSmall,
                    color = when (stock.direction) {
                        PriceDirectionUi.UP -> Color(0xFF4CAF50)
                        PriceDirectionUi.DOWN -> Color(0xFFF44336)
                        PriceDirectionUi.NEUTRAL -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // Price
            Text(
                text = formatPrice(stock.currentPrice),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
