package com.multibank.pricetracker.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.roundToInt
import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

private const val PRICE_UPDATE_INTERVAL_MS = 2000L

/**
 * Produces a flow of simulated "SYMBOL:PRICE" messages at a fixed interval.
 * Maintains internal price state per symbol.
 */
@Singleton
class PriceFeedSimulator @Inject constructor() {

    private val currentPrices = mutableMapOf<String, Double>()

    fun run(symbols: List<StockSymbolDto>): Flow<String> = flow {
        symbols.forEach {
            currentPrices[it.symbol] = it.currentPrice
        }
        while (true) {
            symbols.forEach { stock ->
                val oldPrice = currentPrices[stock.symbol] ?: stock.currentPrice
                val changePercent = Random.nextDouble(-2.0, 2.0)
                val newPrice = (oldPrice * (1 + changePercent / 100.0)).coerceAtLeast(1.0)
                val rounded = (newPrice * 100.0).roundToInt() / 100.0
                currentPrices[stock.symbol] = rounded
                emit("${stock.symbol}:$rounded")
            }
            delay(PRICE_UPDATE_INTERVAL_MS)
        }
    }
}
