package com.multibank.pricetracker.ui.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.multibank.pricetracker.ui.feed.model.ConnectionStateUi
import com.multibank.pricetracker.ui.feed.model.FeedItemUi
import com.multibank.pricetracker.ui.feed.model.PriceDirectionUi
import com.multibank.pricetracker.util.formatPrice
import com.multibank.pricetracker.util.formatPriceChange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolDetailScreen(
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val stock = uiState.stock

    val defaultBg = MaterialTheme.colorScheme.background
    val flashGreen = Color(0xFF4CAF50).copy(alpha = 0.10f)
    val flashRed = Color(0xFFF44336).copy(alpha = 0.10f)

    val targetBg = when (uiState.flash) {
        true  -> flashGreen
        false -> flashRed
        null  -> defaultBg
    }
    val animatedBg by animateColorAsState(
        targetValue = targetBg,
        animationSpec = tween(durationMillis = 600, easing = EaseInOut),
        label = "detailFlash"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stock?.symbol ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBg)
                .padding(innerPadding)
        ) {
            if (stock != null) {
                DetailContent(stock = stock, connectionState = uiState.connectionState)
            }
        }
    }
}

@Composable
private fun DetailContent(
    stock: FeedItemUi,
    connectionState: ConnectionStateUi
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Price card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatPrice(stock.currentPrice),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    val arrow = when (stock.direction) {
                        PriceDirectionUi.UP      -> "↑"
                        PriceDirectionUi.DOWN    -> "↓"
                        PriceDirectionUi.NEUTRAL -> "—"
                    }
                    val arrowColor = when (stock.direction) {
                        PriceDirectionUi.UP      -> Color(0xFF4CAF50)
                        PriceDirectionUi.DOWN    -> Color(0xFFF44336)
                        PriceDirectionUi.NEUTRAL -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(
                        text = arrow,
                        fontSize = 36.sp,
                        color = arrowColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                val changeColor = when (stock.direction) {
                    PriceDirectionUi.UP      -> Color(0xFF4CAF50)
                    PriceDirectionUi.DOWN    -> Color(0xFFF44336)
                    PriceDirectionUi.NEUTRAL -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                Text(
                    text = formatPriceChange(stock.priceChange, stock.priceChangePercent),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = changeColor
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // Connection state
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val dotColor = when (connectionState) {
                        is ConnectionStateUi.Connected    -> Color(0xFF4CAF50)
                        is ConnectionStateUi.Disconnected -> Color(0xFFF44336)
                        is ConnectionStateUi.Connecting   -> Color(0xFFFF9800)
                    }
                    val stateLabel = when (connectionState) {
                        is ConnectionStateUi.Connected    -> "Live"
                        is ConnectionStateUi.Disconnected -> "Disconnected"
                        is ConnectionStateUi.Connecting   -> "Connecting…"
                    }
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stateLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Description card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "About ${stock.symbol}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stock.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }

        // Stats card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Market Stats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatRow(label = "Symbol", value = stock.symbol)
                StatRow(label = "Current Price", value = formatPrice(stock.currentPrice))
                StatRow(label = "Previous Price", value = if (stock.previousPrice > 0) formatPrice(stock.previousPrice) else "—")
                StatRow(label = "Change", value = formatPriceChange(stock.priceChange, stock.priceChangePercent))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Prices update every 2 seconds via WebSocket echo",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
