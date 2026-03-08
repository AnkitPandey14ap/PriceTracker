package com.multibank.pricetracker.ui.feature.feed.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multibank.pricetracker.ui.feature.feed.bean.FeedItemUi
import com.multibank.pricetracker.ui.util.formatPrice
import com.multibank.pricetracker.ui.util.formatPriceChange

/**
 * Single stock row card: rank, symbol with direction/change, and price.
 */
@Composable
fun StockRow(
    stock: FeedItemUi,
    rank: Int,
    flashState: Boolean?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
        shape = RoundedCornerShape(CornerSize(4)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = animatedCardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RankBadge(rank = rank)
            Spacer(modifier = Modifier.width(8.dp))
            StockSymbolBlock(stock = stock, modifier = Modifier.weight(1f))
            StockPriceText(price = stock.currentPrice)
        }
    }
}

@Composable
private fun RankBadge(rank: Int) {
    Text(
        text = "#$rank",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.width(32.dp)
    )
}

@Composable
private fun StockSymbolBlock(stock: FeedItemUi, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stock.symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(6.dp))
            DirectionArrow(direction = stock.direction, fontSize = 16.sp)
        }
        Text(
            text = formatPriceChange(stock.priceChange, stock.priceChangePercent),
            style = MaterialTheme.typography.labelSmall,
            color = directionColor(stock.direction)
        )
    }
}

@Composable
private fun StockPriceText(price: Double) {
    Text(
        text = formatPrice(price),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}
