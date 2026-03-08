package com.multibank.pricetracker.domain

import com.multibank.pricetracker.domain.model.StockSymbol
import javax.inject.Inject

class GetInitialSymbolsUseCase @Inject constructor(
    private val repository: PriceFeedRepository
) {
    operator fun invoke(): List<StockSymbol> = repository.getInitialSymbols()
}
