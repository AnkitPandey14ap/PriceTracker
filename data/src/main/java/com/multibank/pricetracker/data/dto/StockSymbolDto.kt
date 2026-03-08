package com.multibank.pricetracker.data.dto

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
