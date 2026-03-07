package com.multibank.pricetracker.domain

import com.multibank.pricetracker.data.PriceUpdate
import com.multibank.pricetracker.data.StockSymbol
import com.multibank.pricetracker.ui.feed.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface PriceFeedRepository {

    val priceUpdates: Flow<PriceUpdate>

    val connectionState: StateFlow<ConnectionState>

    fun start(symbols: List<StockSymbol>)

    fun stop()
}