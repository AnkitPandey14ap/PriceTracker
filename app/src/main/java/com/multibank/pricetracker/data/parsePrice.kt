package com.multibank.pricetracker.data

import android.util.Log

fun parsePrice(message: String): PriceUpdate? {

    return try {

        val parts = message.trim().split(":")

        if (parts.size != 2) return null

        val symbol = parts[0].trim()

        val price = parts[1].trim().toDoubleOrNull() ?: return null

        PriceUpdate(
            symbol = symbol,
            price = price
        )

    } catch (e: Exception) {

        Log.e("PriceParser", "Failed to parse message: $message", e)

        null
    }
}