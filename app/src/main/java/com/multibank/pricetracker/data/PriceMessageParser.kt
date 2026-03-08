package com.multibank.pricetracker.data

/**
 * Pure parser for "SYMBOL:PRICE" WebSocket messages. No side effects (no logging).
 */
object PriceMessageParser {

    fun parse(message: String): PriceUpdateDto? {
        val parts = message.trim().split(":")
        if (parts.size != 2) return null
        val symbol = parts[0].trim()
        val price = parts[1].trim().toDoubleOrNull() ?: return null
        return PriceUpdateDto(symbol = symbol, price = price)
    }
}
