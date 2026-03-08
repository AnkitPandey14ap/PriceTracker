package com.multibank.pricetracker.data.dto

/**
 * Data-layer enum for price direction. Map to domain [com.multibank.pricetracker.domain.model.PriceDirectionEntity] via [data.mapper.StockSymbolMapper].
 */
enum class PriceDirectionDto {
    UP, DOWN, NEUTRAL
}
