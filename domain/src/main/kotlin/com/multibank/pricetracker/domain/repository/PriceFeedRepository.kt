package com.multibank.pricetracker.domain.repository

import com.multibank.pricetracker.domain.model.ConnectionStateEntity
import com.multibank.pricetracker.domain.model.PriceUpdateEntity
import com.multibank.pricetracker.domain.model.StockSymbolEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface PriceFeedRepository {

    val priceUpdates: Flow<PriceUpdateEntity>

    val connectionState: StateFlow<ConnectionStateEntity>

    /** User-friendly error messages (e.g. for toasts). */
    val errorMessages: SharedFlow<String>

    fun getInitialSymbols(): List<StockSymbolEntity>

    fun start(symbols: List<StockSymbolEntity>)

    fun stop()
}
