package com.multibank.pricetracker.domain

import com.multibank.pricetracker.domain.model.ConnectionState
import com.multibank.pricetracker.domain.model.PriceUpdate
import com.multibank.pricetracker.domain.model.StockSymbol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface PriceFeedRepository {

    val priceUpdates: Flow<PriceUpdate>

    val connectionState: StateFlow<ConnectionState>

    /** User-friendly error messages (e.g. for toasts). */
    val errorMessages: SharedFlow<String>

    fun getInitialSymbols(): List<StockSymbol>

    fun start(symbols: List<StockSymbol>)

    fun stop()
}
