package com.multibank.pricetracker.domain.model

data class StockSymbolEntity(
    val symbol: String,
    val currentPrice: Double,
    val previousPrice: Double,
    val direction: PriceDirectionEntity = PriceDirectionEntity.NEUTRAL,
    val description: String = ""
)
