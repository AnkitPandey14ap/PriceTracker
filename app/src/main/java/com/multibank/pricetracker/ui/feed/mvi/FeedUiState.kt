package com.multibank.pricetracker.ui.feed.mvi

import com.multibank.pricetracker.ui.feed.bean.ConnectionStateUi
import com.multibank.pricetracker.ui.feed.bean.FeedItemUi

data class FeedUiState(
    val stocks: List<FeedItemUi> = emptyList(),
    val connectionState: ConnectionStateUi = ConnectionStateUi.Disconnected,
    val isFeedRunning: Boolean = false,
    val flashMap: Map<String, Boolean?> = emptyMap()
)