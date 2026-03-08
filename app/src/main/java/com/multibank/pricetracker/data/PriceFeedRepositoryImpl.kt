package com.multibank.pricetracker.data

import com.multibank.pricetracker.data.mapper.PriceUpdateMapper
import com.multibank.pricetracker.data.mapper.StockSymbolMapper
import com.multibank.pricetracker.data.buildInitialStocks
import com.multibank.pricetracker.domain.PriceFeedRepository
import com.multibank.pricetracker.domain.model.ConnectionState
import com.multibank.pricetracker.domain.model.PriceUpdate
import com.multibank.pricetracker.domain.model.StockSymbol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class PriceFeedRepositoryImpl @Inject constructor(
    private val webSocketService: WebSocketService
) : PriceFeedRepository {

    override val connectionState: StateFlow<ConnectionState>
        get() = webSocketService.connectionState

    override val errorMessages: SharedFlow<String>
        get() = webSocketService.errorMessages

    override fun getInitialSymbols(): List<StockSymbol> =
        buildInitialStocks().map { StockSymbolMapper.toDomain(it) }

    override val priceUpdates: Flow<PriceUpdate> =
        webSocketService.messages.mapNotNull { PriceMessageParser.parse(it) }.map { PriceUpdateMapper.toDomain(it) }

    override fun start(symbols: List<StockSymbol>) {
        webSocketService.start(symbols.map { StockSymbolMapper.toDto(it) })
    }

    override fun stop() {
        webSocketService.stop()
    }
}