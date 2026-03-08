package com.multibank.pricetracker.ui.feed.bean

data class FeedItemUi(
    val symbol: String,
    val currentPrice: Double,
    val previousPrice: Double,
    val direction: PriceDirectionUi = PriceDirectionUi.NEUTRAL,
    val description: String = ""
) {
    val priceChange: Double get() = currentPrice - previousPrice
    val priceChangePercent: Double
        get() = if (previousPrice != 0.0) (priceChange / previousPrice) * 100.0 else 0.0
}

enum class PriceDirectionUi {
    UP, DOWN, NEUTRAL
}
