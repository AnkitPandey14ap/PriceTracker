package com.multibank.pricetracker.ui.feed

import com.multibank.pricetracker.data.StockSymbol

data class FeedUiState(
    val stocks: List<StockSymbol> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.Disconnected,
    val isFeedRunning: Boolean = false,
    // symbol -> flash state (true=green flash, false=red flash, null=none)
    val flashMap: Map<String, Boolean?> = emptyMap()
)