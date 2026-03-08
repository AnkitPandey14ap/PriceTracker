package com.multibank.pricetracker.data.datasource.websocket

import com.multibank.pricetracker.data.datasource.PriceMessageParser
import com.multibank.pricetracker.data.dto.PriceUpdateDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Parses "SYMBOL:PRICE" WebSocket messages. No side effects (no logging).
 */
@Singleton
class DefaultPriceMessageParser @Inject constructor() : PriceMessageParser {

    override fun parse(message: String): PriceUpdateDto? {
        val parts = message.trim().split(":")
        if (parts.size != 2) return null
        val symbol = parts[0].trim()
        val price = parts[1].trim().toDoubleOrNull() ?: return null
        return PriceUpdateDto(symbol = symbol, price = price)
    }
}
