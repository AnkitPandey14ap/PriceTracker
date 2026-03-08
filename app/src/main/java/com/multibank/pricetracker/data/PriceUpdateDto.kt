package com.multibank.pricetracker.data

/**
 * Data-layer model for a single price update (e.g. parsed from WebSocket).
 * Map to [com.multibank.pricetracker.domain.model.PriceUpdate] via [data.mapper.PriceUpdateMapper].
 */
data class PriceUpdateDto(val symbol: String, val price: Double)
