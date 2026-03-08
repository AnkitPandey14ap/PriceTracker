package com.multibank.pricetracker.domain.usecase

import com.multibank.pricetracker.domain.repository.PriceFeedRepository
import com.multibank.pricetracker.domain.model.StockSymbolEntity
import javax.inject.Inject

class StartFeedUseCase @Inject constructor(
    private val repository: PriceFeedRepository
) {
    operator fun invoke(symbols: List<StockSymbolEntity>) {
        repository.start(symbols)
    }
}