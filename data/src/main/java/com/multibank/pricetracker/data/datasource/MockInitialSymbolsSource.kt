package com.multibank.pricetracker.data.datasource

import com.multibank.pricetracker.data.mapper.StockSymbolMapper
import com.multibank.pricetracker.data.mock.buildInitialStocks
import com.multibank.pricetracker.domain.model.StockSymbolEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockInitialSymbolsSource @Inject constructor() : InitialSymbolsSource {

    override fun getInitialSymbols(): List<StockSymbolEntity> =
        buildInitialStocks().map { StockSymbolMapper.toDomain(it) }
}
