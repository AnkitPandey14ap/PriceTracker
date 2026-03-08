package com.multibank.pricetracker.data.datasource

import com.multibank.pricetracker.domain.model.ConnectionStateEntity
import com.multibank.pricetracker.domain.model.PriceUpdateEntity
import com.multibank.pricetracker.domain.model.StockSymbolEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Abstraction for a live price feed (e.g. WebSocket, REST polling).
 * Implementations handle connection, parsing, and error emission.
 */
interface PriceFeedDataSource {

    val connectionState: StateFlow<ConnectionStateEntity>

    /** User-facing error messages (e.g. for toasts). */
    val errorMessages: SharedFlow<String>

    /** Parsed price updates in domain form. */
    val priceUpdates: Flow<PriceUpdateEntity>

    fun start(symbols: List<StockSymbolEntity>)

    fun stop()
}
