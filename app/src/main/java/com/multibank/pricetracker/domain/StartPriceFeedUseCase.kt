package com.multibank.pricetracker.domain

import com.multibank.pricetracker.data.StockSymbol
import javax.inject.Inject

class StartFeedUseCase @Inject constructor(
    private val repository: PriceFeedRepository
) {
    operator fun invoke(symbols: List<StockSymbol>) {
        repository.start(symbols)
    }
}