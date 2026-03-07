package com.multibank.pricetracker.data

import com.multibank.pricetracker.domain.PriceFeedRepository
import com.multibank.pricetracker.ui.feed.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class PriceFeedRepositoryImpl @Inject constructor(
    private val webSocketService: WebSocketService
) : PriceFeedRepository {

    override val connectionState: StateFlow<ConnectionState>
        get() = webSocketService.connectionState

    override val priceUpdates: Flow<PriceUpdate> =
        webSocketService.messages.mapNotNull { parsePrice(it) }

    override fun start(symbols: List<StockSymbol>) {
        webSocketService.start(symbols)
    }

    override fun stop() {
        webSocketService.stop()
    }
}