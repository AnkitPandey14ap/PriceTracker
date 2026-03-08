package com.multibank.pricetracker.ui.feature.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.multibank.pricetracker.ui.feature.feed.bean.FeedItemUi

/**
 * Lazy list of stock rows with optional flash state per symbol.
 */
@Composable
fun FeedStockList(
    stocks: List<FeedItemUi>,
    flashMap: Map<String, Boolean?>,
    onSymbolClick: (String) -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }
        itemsIndexed(
            items = stocks,
            key = { _, stock -> stock.symbol }
        ) { index, stock ->
            val flashState = flashMap[stock.symbol]
            StockRow(
                stock = stock,
                rank = index + 1,
                flashState = flashState,
                onClick = { onSymbolClick(stock.symbol) },
                modifier = Modifier.semantics { testTag = "feed_stock_row_${stock.symbol}" }
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
