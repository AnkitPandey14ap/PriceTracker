package com.multibank.pricetracker.data.datasource

import com.multibank.pricetracker.domain.model.StockSymbolEntity

/**
 * Provides the initial list of symbols to display and subscribe to.
 * Implementations may use static mock data, an API, or local storage.
 */
interface InitialSymbolsSource {

    fun getInitialSymbols(): List<StockSymbolEntity>
}
