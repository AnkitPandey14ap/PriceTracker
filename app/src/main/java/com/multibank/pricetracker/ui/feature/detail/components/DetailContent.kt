package com.multibank.pricetracker.ui.feature.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multibank.pricetracker.ui.feature.feed.bean.ConnectionStateUi
import com.multibank.pricetracker.ui.feature.feed.bean.FeedItemUi

/**
 * Main content column for detail screen: price card, about card, market stats, footer.
 */
@Composable
fun DetailContent(
    stock: FeedItemUi,
    connectionState: ConnectionStateUi
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        DetailPriceCard(stock = stock, connectionState = connectionState)
        AboutCard(stock = stock)
        MarketStatsCard(stock = stock)
        Spacer(modifier = Modifier.height(8.dp))
        DetailFooter(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}
