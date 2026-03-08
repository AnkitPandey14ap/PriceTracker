package com.multibank.pricetracker.ui.feature.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multibank.pricetracker.R
import com.multibank.pricetracker.ui.feature.feed.bean.FeedItemUi
import com.multibank.pricetracker.ui.util.formatPrice
import com.multibank.pricetracker.ui.util.formatPriceChange

/**
 * Card showing "Market Stats" and key-value rows (Symbol, Current Price, etc.).
 */
@Composable
fun MarketStatsCard(stock: FeedItemUi) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { testTag = "detail_market_stats" },
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
                text = stringResource(R.string.detail_market_stats),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            StatRow(label = stringResource(R.string.detail_label_symbol), value = stock.symbol)
            StatRow(
                label = stringResource(R.string.detail_label_current_price),
                value = formatPrice(stock.currentPrice),
                valueTestTag = "detail_stats_current_price"
            )
            StatRow(
                label = stringResource(R.string.detail_label_previous_price),
                value = if (stock.previousPrice > 0) formatPrice(stock.previousPrice) else stringResource(R.string.detail_previous_price_na)
            )
            StatRow(
                label = stringResource(R.string.detail_label_change),
                value = formatPriceChange(stock.priceChange, stock.priceChangePercent)
            )
        }
    }
}
