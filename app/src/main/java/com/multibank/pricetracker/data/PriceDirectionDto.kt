package com.multibank.pricetracker.data

/**
 * Data-layer enum for price direction. Map to domain [com.multibank.pricetracker.domain.model.PriceDirection] via [data.mapper.StockSymbolMapper].
 */
enum class PriceDirectionDto {
    UP, DOWN, NEUTRAL
}
