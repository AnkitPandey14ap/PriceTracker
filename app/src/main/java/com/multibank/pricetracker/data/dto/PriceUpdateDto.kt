package com.multibank.pricetracker.data.dto

/**
 * Data-layer model for a single price update (e.g. parsed from WebSocket).
 * Map to [com.multibank.pricetracker.domain.model.PriceUpdateEntity] via [data.mapper.PriceUpdateMapper].
 */
data class PriceUpdateDto(val symbol: String, val price: Double)
