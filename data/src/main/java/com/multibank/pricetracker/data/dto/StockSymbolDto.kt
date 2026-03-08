package com.multibank.pricetracker.data.dto

data class StockSymbolDto(
    val symbol: String,
    val currentPrice: Double,
    val previousPrice: Double,
    val direction: PriceDirectionDto = PriceDirectionDto.NEUTRAL,
    val description: String = ""
)
