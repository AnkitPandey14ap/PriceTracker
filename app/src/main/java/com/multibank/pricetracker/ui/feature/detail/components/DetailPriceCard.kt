package com.multibank.pricetracker.ui.feature.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multibank.pricetracker.ui.feature.feed.bean.ConnectionStateUi
import com.multibank.pricetracker.ui.feature.feed.bean.FeedItemUi
import com.multibank.pricetracker.ui.feature.feed.components.directionColor
import com.multibank.pricetracker.ui.feature.feed.components.DirectionArrow
import com.multibank.pricetracker.ui.util.formatPrice
import com.multibank.pricetracker.ui.util.formatPriceChange

/**
 * Card showing current price, direction arrow, change text, and connection status.
 */
@Composable
fun DetailPriceCard(
    stock: FeedItemUi,
    connectionState: ConnectionStateUi
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { testTag = "detail_price_card" },
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
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.semantics { testTag = "detail_price_value" }
                )
                Spacer(modifier = Modifier.width(12.dp))
                DirectionArrow(direction = stock.direction, fontSize = 36.sp)
            }

            Text(
                text = formatPriceChange(stock.priceChange, stock.priceChangePercent),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = directionColor(stock.direction),
                modifier = Modifier.semantics { testTag = "detail_change_value" }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            ConnectionStatusRow(connectionState = connectionState)
        }
    }
}
