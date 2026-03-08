package com.multibank.pricetracker.ui.feed

import com.multibank.pricetracker.ui.feed.model.ConnectionStateUi
import com.multibank.pricetracker.ui.feed.model.FeedItemUi

data class FeedUiState(
    val stocks: List<FeedItemUi> = emptyList(),
    val connectionState: ConnectionStateUi = ConnectionStateUi.Disconnected,
    val isFeedRunning: Boolean = false,
    val flashMap: Map<String, Boolean?> = emptyMap()
)
