package com.multibank.pricetracker.data

/**
 * Data-layer model for a stock symbol (e.g. from API or seed data).
 * Map to domain [com.multibank.pricetracker.domain.model.StockSymbol] via [data.mapper.StockSymbolMapper].
 */
data class StockSymbolDto(
    val symbol: String,
    val currentPrice: Double,
    val previousPrice: Double,
    val direction: PriceDirectionDto = PriceDirectionDto.NEUTRAL,
    val description: String = ""
) {
    val priceChange: Double get() = currentPrice - previousPrice
    val priceChangePercent: Double
        get() = if (previousPrice != 0.0) (priceChange / previousPrice) * 100.0 else 0.0
}
