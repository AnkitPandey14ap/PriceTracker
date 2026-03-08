package com.multibank.pricetracker.data.repository

import com.multibank.pricetracker.data.datasource.websocket.PriceMessageParser
import com.multibank.pricetracker.data.datasource.websocket.WebSocketService
import com.multibank.pricetracker.data.mapper.PriceUpdateMapper
import com.multibank.pricetracker.data.mapper.StockSymbolMapper
import com.multibank.pricetracker.domain.repository.PriceFeedRepository
import com.multibank.pricetracker.domain.model.ConnectionStateEntity
import com.multibank.pricetracker.domain.model.PriceUpdateEntity
import com.multibank.pricetracker.domain.model.StockSymbolEntity
import com.multibank.pricetracker.data.mock.buildInitialStocks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class PriceFeedRepositoryImpl @Inject constructor(
    private val webSocketService: WebSocketService
) : PriceFeedRepository {

    override val connectionState: StateFlow<ConnectionStateEntity>
        get() = webSocketService.connectionState

    override val errorMessages: SharedFlow<String>
        get() = webSocketService.errorMessages

    override fun getInitialSymbols(): List<StockSymbolEntity> =
        buildInitialStocks().map { StockSymbolMapper.toDomain(it) }

    override val priceUpdates: Flow<PriceUpdateEntity> =
        webSocketService.messages.mapNotNull { PriceMessageParser.parse(it) }.map { PriceUpdateMapper.toDomain(it) }

    override fun start(symbols: List<StockSymbolEntity>) {
        webSocketService.start(symbols.map { StockSymbolMapper.toDto(it) })
    }

    override fun stop() {
        webSocketService.stop()
    }
}