package com.multibank.pricetracker.data.repository

import com.multibank.pricetracker.data.datasource.InitialSymbolsSource
import com.multibank.pricetracker.data.datasource.PriceFeedDataSource
import com.multibank.pricetracker.domain.repository.PriceFeedRepository
import com.multibank.pricetracker.domain.model.ConnectionStateEntity
import com.multibank.pricetracker.domain.model.PriceUpdateEntity
import com.multibank.pricetracker.domain.model.StockSymbolEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PriceFeedRepositoryImpl @Inject constructor(
    private val priceFeedDataSource: PriceFeedDataSource,
    private val initialSymbolsSource: InitialSymbolsSource
) : PriceFeedRepository {

    override val connectionState: StateFlow<ConnectionStateEntity>
        get() = priceFeedDataSource.connectionState

    override val errorMessages: SharedFlow<String>
        get() = priceFeedDataSource.errorMessages

    override fun getInitialSymbols(): List<StockSymbolEntity> =
        initialSymbolsSource.getInitialSymbols()

    override val priceUpdates: Flow<PriceUpdateEntity> =
        priceFeedDataSource.priceUpdates

    override fun start(symbols: List<StockSymbolEntity>) {
        priceFeedDataSource.start(symbols)
    }

    override fun stop() {
        priceFeedDataSource.stop()
    }
}
