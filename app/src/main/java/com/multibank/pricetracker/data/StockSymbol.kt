package com.multibank.pricetracker.data

enum class PriceDirection {
    UP, DOWN, NEUTRAL
}

data class StockSymbol(
    val symbol: String,
    val currentPrice: Double,
    val previousPrice: Double,
    val direction: PriceDirection = PriceDirection.NEUTRAL,
    val description: String = ""
) {
    val priceChange: Double get() = currentPrice - previousPrice
    val priceChangePercent: Double
        get() = if (previousPrice != 0.0) (priceChange / previousPrice) * 100.0 else 0.0
}