package com.multibank.pricetracker.data.datasource

import com.multibank.pricetracker.data.dto.PriceUpdateDto

/**
 * Parses raw feed messages (e.g. WebSocket text) into [PriceUpdateDto].
 * Implementations can support different wire formats without changing consumers.
 */
fun interface PriceMessageParser {

    fun parse(message: String): PriceUpdateDto?
}
